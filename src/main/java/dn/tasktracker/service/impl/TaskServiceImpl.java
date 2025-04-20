package dn.tasktracker.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import dn.tasktracker.dto.ListTaskResponse;
import dn.tasktracker.dto.TaskRequest;
import dn.tasktracker.dto.TaskResponse;
import dn.tasktracker.dto.TaskSortDto;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.TaskStatus;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.event.TaskCreateEvent;
import dn.tasktracker.event.TaskUpdatedEvent;
import dn.tasktracker.exception.TaskNotFoundException;
import dn.tasktracker.exception.UserNotFoundException;
import dn.tasktracker.mapper.TaskMapper;
import dn.tasktracker.mapper.UserMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.TaskSpecification;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.TaskService;
import dn.tasktracker.utils.DateTimeUtil;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
@Log4j2
@CacheConfig(cacheManager = "redisCacheManager")
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    @Value("${app.cache.caches.taskAfterCreate.ttl}")
    private Duration ttl;
    @Value("${spring.cache.cache-names}")
    private List<String> cacheNames;



    @Override
    public ListTaskResponse getAll(TaskSortDto taskDto) {
        return taskMapper.mapToResponseList(taskRepository.findAll(
                TaskSpecification.withFilter(taskDto),
                PageRequest.of(taskDto.getPageNumber(), taskDto.getPageSize()))
                .getContent()
                .stream()
                .peek(tasks->{
                    redisTemplate.opsForList().leftPush(cacheNames.get(1), tasks.toString());
                    redisTemplate.expire(cacheNames.get(1), ttl.toMinutes(), TimeUnit.MINUTES);
                }).toList());
    }

    @Override
    public ListTaskResponse getAll() {
        return taskMapper.mapToResponseList(taskRepository.findAll()
                .stream()
                .peek(tasks->{
                    redisTemplate.opsForList().leftPush(cacheNames.get(3), tasks.toString());
                    redisTemplate.expire(cacheNames.get(3), ttl.toMinutes(), TimeUnit.MINUTES);
                }).toList());
    }


    @Override
    public TaskResponse getById(final Long id) {
        return taskMapper.toDto(taskRepository.findById(id)
                        .stream()
                        .peek(task->{
                            redisTemplate.opsForValue().set(String.valueOf(id), task.toString(), ttl);
                            log.info("Task: {} is written to redis",task.toString());
                        }).findAny()
                          .orElseThrow(()->new TaskNotFoundException(
                          MessageFormat.format("Task with id: {0} not found",id)
                        )));}

    @Override
    @Transactional
    public Map<Long,List<TaskEntity>>  setTimeForTask(Long userId, Long taskId, Long time) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        MessageFormat.format("User with id {0} not found!", userId)));

        Map<Long,List<TaskEntity>> taskMap = taskRepository.findAll()
                .stream()
                .flatMap(taskEntity -> taskEntity.getUsers().stream())
                .filter(userEntity -> userEntity.getId().equals(userId))
                .map(UserEntity::getTasks)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(TaskEntity::getId,
                        Collectors.filtering(TaskEntity::isCompletedAt,
                                Collectors.toList())));
        redisTemplate.opsForSet().add(String.valueOf(user.getId()),taskMap);
        redisTemplate.expire(String.valueOf(user.getId()),ttl.toMinutes(),TimeUnit.MINUTES);
        log.info("Tasks is: {}",taskMap.values());
        return taskMap;

    }

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_READ)
    @Transactional
    public TaskResponse save(TaskRequest taskRequest) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskEntity.getId());
        taskEntity.setTitle(taskRequest.getTitle());
        taskEntity.setDescription(taskRequest.getDescription());
        taskEntity.setStatus(String.valueOf(TaskStatus.IN_PROGRESS));
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setCompletedAt(false);
        taskEntity.setUserId(taskRequest.getUserId());
        taskEntity.setRating(taskRequest.getRating());
        userRepository.findById(
                taskEntity.getUserId())
               .ifPresent(u->u.addTask(taskEntity));
        taskRepository.save(taskEntity);
        eventPublisher.publishEvent(
                new TaskCreateEvent(
                        taskEntity.getId(),
                        taskEntity.getTitle(),
                        taskEntity.getDescription(),
                        taskEntity.getStatus()
                )
        );
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(taskEntity.getId());
        taskResponse.setTitle(taskEntity.getTitle());
        taskResponse.setDescription(taskEntity.getDescription());
        taskResponse.setStatus(taskEntity.getStatus());
        taskResponse.setCreatedAt(taskEntity.getCreatedAt());
        taskResponse.setUpdatedAt(taskEntity.getUpdatedAt());
        taskResponse.setUsers(taskEntity.getUsers());
        taskResponse.setRating(taskEntity.getRating());

        log.info("Task: {} is saved",taskEntity.toString());
        try {
            TaskResponse task =  taskMapper.toDto(taskEntity);
            String taskAsJsonString = objectMapper.writeValueAsString(task);
            redisTemplate.opsForValue().setIfAbsent(String.valueOf(task.getId()), taskAsJsonString, ttl);
            log.info("Task: {} is created", task.toString());
            return task;
        }catch (Exception e){
            log.error("Error writing value in redis: {}",e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public void setTaskForUser(TaskRequest taskRequest, String userId) {

    }


    @Override
    public TaskResponse findByTitle(String title) {
        return taskMapper.toDto(taskRepository.findByTitle(title)
                .orElseThrow(()->new TaskNotFoundException(
                        MessageFormat.format("Task with title {0} not found",title)
                )));
    }


    @Override
    @Transactional
    @SneakyThrows
    public void update(final Long id, final TaskRequest taskRequest) {
        taskRepository.findById(id)
                .map(task->{
                            task.setTitle(Optional.ofNullable(task.getTitle())
                                    .orElse(taskRequest.getTitle()));
                            task.setDescription(Optional.ofNullable(task.getDescription())
                                    .orElse(taskRequest.getTitle()));
                            task.setStatus(Optional.ofNullable(task.getStatus())
                                    .orElse(taskRequest.getTitle()));
                            task.setUpdatedAt(LocalDateTime.now());
                            taskRepository.save(task);
                            eventPublisher.publishEvent(
                                    new TaskUpdatedEvent(
                                            task.getId(),
                                            task.getTitle(),
                                            task.getDescription(),
                                            task.getStatus(),
                                            task.getUpdatedAt()));
                            return task;
                        }).orElseThrow(() -> new TaskNotFoundException(
                        MessageFormat.format("Task with id: {0} not found", id)));
    }

    @Override
    public void deleteById(final Long id) {
        taskRepository.findById(id)
                .ifPresentOrElse(taskEntity -> {
                    taskRepository.delete(taskEntity);
                    redisTemplate.opsForValue().getAndDelete(String.valueOf(id));
                    eventPublisher.publishEvent(new TaskCreateEvent(
                            taskEntity.getId(),
                            taskEntity.getTitle(),
                            taskEntity.getDescription(),
                            taskEntity.getStatus()));
                    log.info("Id of deleted task: {}", taskEntity.getId());
                },()->{
                    throw new TaskNotFoundException(
                            MessageFormat.format("Task with id: {0} not found",id));
                });

    }

    @Override
    public void deleteAllByIds(List<Long> ids) {
        List<TaskEntity> taskEntities = taskRepository.findAllById(ids);
        taskEntities.forEach(taskEntity -> {
            taskRepository.deleteAllInBatch(taskEntities);
            log.info("Tasks for deleting task: {}", taskEntity.getId());
        });

    }



}

