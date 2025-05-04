package dn.tasktracker.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dn.tasktracker.dto.ListTaskResponse;
import dn.tasktracker.dto.TaskRequest;
import dn.tasktracker.dto.TaskResponse;
import dn.tasktracker.dto.TaskSortDto;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.TaskStatus;
import dn.tasktracker.event.TaskCreateEvent;
import dn.tasktracker.exception.TaskNotFoundException;
import dn.tasktracker.exception.UserNotFoundException;
import dn.tasktracker.mapper.TaskMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.TaskSpecification;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


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
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final String USER_NOT_FOUND = "User with id {0} not found!";
    private static final String TASK_WITH_ID_NOT_FOUND = "Task with id: {0} not found";
    private static final String TASK_WITH_TITLE_NOT_FOUND  = "Task with title: {0} not found";


    @Value("${app.cache.caches.taskAfterCreate.ttl}")
    private Duration ttl;
    @Value("${spring.cache.cache-names}")
    private List<String> cacheNames;
    @Value("${messaging.queue.name}")
    private String queueName;




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
                        }).findAny()
                          .orElseThrow(()->new TaskNotFoundException(
                          MessageFormat.format(TASK_WITH_ID_NOT_FOUND,id)
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
        taskEntity.setUser(userRepository.findById(taskRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        MessageFormat.format(USER_NOT_FOUND, taskRequest.getUserId()))));
        var user = taskEntity.getUser();
        user.addTask(taskEntity);
        taskRepository.save(taskEntity);
        var event = createEvent(taskEntity);
        eventPublisher.publishEvent(event);
        writeToRedis(taskEntity);
        rabbitTemplate.convertAndSend(queueName,event);
        log.info("Event: {} is saved",event);
        return taskEntity;
    }


    @Override
    public TaskResponse findByTitle(String title) {
        return taskMapper.toDto(taskRepository.findByTitle(title)
                .orElseThrow(()->new TaskNotFoundException(
                        MessageFormat.format(TASK_WITH_TITLE_NOT_FOUND,title))));
    }

    @Override
    @Transactional
    public void update(Long id,
                       final String status,
                       Long userId) {
        var task = taskRepository.findById(id)
                        .orElseThrow(()->new TaskNotFoundException(
                                MessageFormat.format(TASK_WITH_ID_NOT_FOUND,id)));
        var user = userRepository.findById(userId)
                        .orElseThrow(()->new UserNotFoundException(
                                MessageFormat.format(USER_NOT_FOUND, userId)));

        task.setUser(user);
        boolean isValidStatus = validStatus(status);
        if (isValidStatus) {
            task.setStatus(status.trim().toUpperCase());
            taskRepository.save(task);
            task.setUpdatedAt(LocalDateTime.now());
            var event = createEvent(task);
            eventPublisher.publishEvent(event);
            writeToRedis(task);
            rabbitTemplate.convertAndSend(queueName,event);
        }
        else {
            throw new RuntimeException("UNKNOWN STATUS");
        }

    }

    @Override
    public void deleteById(final Long id) {
        taskRepository.findById(id)
                .ifPresentOrElse(taskEntity -> {
                    taskRepository.delete(taskEntity);
                    redisTemplate.opsForValue().getAndDelete(String.valueOf(id));
                    var event = createEvent(taskEntity);
                    eventPublisher.publishEvent(event);
                    rabbitTemplate.convertAndSend(queueName,event);
                    log.info("Id of deleted task: {}", taskEntity.getId());
                },()->{
                    throw new TaskNotFoundException(
                            MessageFormat.format(TASK_WITH_ID_NOT_FOUND,id));
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

    private boolean validStatus(String status){
        return status.equals(String.valueOf(TaskStatus.IN_PROGRESS)) ||
                status.equals(String.valueOf(TaskStatus.COMPLETED)) ||
                status.equals(String.valueOf(TaskStatus.EXPIRED));
    }

    private TaskCreateEvent createEvent(TaskEntity taskEntity){
        return new TaskCreateEvent(
                taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getStatus(),
                taskEntity.getUser().getUsername());
    }

    private void writeToRedis(TaskEntity taskEntity){
        try {
            var key = String.valueOf(taskEntity.getId());
            var value = objectMapper.writeValueAsString(taskEntity);
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(String.valueOf(String.valueOf(taskEntity.getId())),ttl.toMinutes(),TimeUnit.MINUTES);
        }  catch (JsonProcessingException | AmqpException e ){
            log.error("Error writing value in redis: {}",e.getLocalizedMessage());
            throw new RuntimeException();
        }
    }



}

