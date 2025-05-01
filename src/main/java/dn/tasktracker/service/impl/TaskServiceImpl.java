package dn.tasktracker.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@CacheConfig(cacheManager = "redisCacheManager")
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final OkHttpClient okHttpClient;
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
                    redisTemplate.expire(cacheNames.get(1), ttl.toMinutes(), TimeUnit.MINUTES);})
                .toList());
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
    public TaskEntity save(TaskRequest taskRequest) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(taskRequest.getTitle().trim());
        taskEntity.setDescription(taskRequest.getDescription());
        taskEntity.setStatus(String.valueOf(TaskStatus.IN_PROGRESS));
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setCompletedAt(false);
        taskEntity.setUser(userRepository
                .findById(taskRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        MessageFormat.format("User with id {0} not found!", taskRequest.getUserId())
                )));
        var user = taskEntity.getUser();
        user.addTask(taskEntity);
        taskRepository.save(taskEntity);
        eventPublisher.publishEvent(
                new TaskCreateEvent(
                        taskEntity.getId(),
                        taskEntity.getTitle(),
                        taskEntity.getDescription(),
                        taskEntity.getStatus(),
                        taskEntity.getUser()
                                .getUsername()));

        log.info("Task: {} is saved",taskEntity);

        try {
            var key = String.valueOf(taskEntity.getId());
            var value = objectMapper.writeValueAsString(taskEntity);
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(String.valueOf(String.valueOf(taskEntity.getId())),ttl.toMinutes(),TimeUnit.MINUTES);
            return taskEntity;
        }catch (JsonProcessingException e){
            log.error("Error writing value in redis: {}",e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public Map<String,List<UserEntity>> setUsersForTask(List<Long> userIds, Long taskId) {
         TaskEntity task =  taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException(MessageFormat.format("Задача с идентификатором {} не найдена", taskId)));

         List<UserEntity> users = userRepository.findAllById(userIds);
         taskRepository.save(task);
         userRepository.saveAll(users);
         log.info("Task created! {}", task);
         return Map.of(task.getTitle(),users);
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
    public void update(Long id,
                       final String status,
                       Long userId) {
        var task = taskRepository.findById(id)
                        .orElseThrow(()->new TaskNotFoundException(
                                MessageFormat.format("Task with id: {0} not found",id)));

        task.setUser(userRepository.findById(userId).orElseThrow(()->new UserNotFoundException(
                MessageFormat.format("User with id {0} not found!", userId))));
        boolean isValidStatus = validStatus(status);
        if (isValidStatus) {
            task.setStatus(status.trim().toUpperCase());
            taskRepository.save(task);
            task.setUpdatedAt(LocalDateTime.now());
            eventPublisher.publishEvent(
                    new TaskUpdatedEvent(
                            task.getId(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getStatus(),
                            task.getUpdatedAt(),
                            task.getUser().getUsername()));
            try {
                var key = String.valueOf(task.getId());
                var value = objectMapper.writeValueAsString(task);
                redisTemplate.opsForValue().set(key, value);
                redisTemplate.expire(String.valueOf(String.valueOf(task.getId())),ttl.toMinutes(),TimeUnit.MINUTES);
            }catch (JsonProcessingException e){
                log.error("Не удалось записать значение в Redis: {}",e.getLocalizedMessage());
            }
        }else {
            throw new RuntimeException("UNKNOWN STATUS");
        }

    }


    private boolean validStatus(String status){
        return status.equals(String.valueOf(TaskStatus.IN_PROGRESS)) ||
                status.equals(String.valueOf(TaskStatus.COMPLETED)) ||
                status.equals(String.valueOf(TaskStatus.FAILED));
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
                            taskEntity.getStatus(),
                            taskEntity.getUser().getUsername()));
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

