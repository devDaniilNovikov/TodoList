package dn.tasktracker.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dn.tasktracker.aop.Loggable;
import dn.tasktracker.dto.*;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.TaskStatus;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.event.*;
import dn.tasktracker.exception.TaskNotFoundException;
import dn.tasktracker.exception.UserNotFoundException;
import dn.tasktracker.mapper.TaskMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.TaskSpecification;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.EmailService;
import dn.tasktracker.service.EventService;
import dn.tasktracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.DeleteEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@Service
@CacheConfig(cacheManager = "redisCacheManager")
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final String EVENT_NAME = "Creating of Task";


    @Value("${app.cache.caches.taskAfterCreate.ttl}")
    private Duration ttl;
    @Value("${spring.cache.cache-names}")
    private List<String> cacheNames;


    @Override
    public ListTaskResponse getAll(TaskSortDto taskDto) {
        return taskMapper.mapToDtoList(taskRepository.findAll(
                        TaskSpecification.withFilter(taskDto),
                        PageRequest.of(taskDto.getPageNumber(),
                                taskDto.getPageSize()))
                .stream()
                .peek(tasks -> {
                    redisTemplate.opsForList().leftPush(cacheNames.get(1), tasks.toString());
                    redisTemplate.expire(cacheNames.get(1), ttl.toMinutes(), TimeUnit.MINUTES);
                }).toList());
    }

    @Override
    public ListTaskResponse getAll() {
        return taskMapper.mapToDtoList(taskRepository.findAll()
                .stream()
                .peek(tasks -> {
                    redisTemplate.opsForList().leftPush(cacheNames.get(3), tasks.toString());
                    redisTemplate.expire(cacheNames.get(3), ttl.toMinutes(), TimeUnit.MINUTES);
                }).toList());
    }


    @Override
    public TaskResponse getById(final Long id) {
        return taskMapper.toDto(taskRepository.findById(id)
                .stream()
                .peek(task -> {
                    redisTemplate.opsForValue()
                            .set(String.valueOf(id), task.toString(), ttl);
                }).findAny()
                .orElseThrow(() -> new TaskNotFoundException(
                        MessageFormat.format("Task with id: {0} not found", id)
                )));
    }


    @Override
    @Transactional
    @Loggable
    public TaskEntity save(TaskRequest taskRequest) {
        TaskEntity taskEntity = new TaskEntity();
        ThreadLocalRandom.current();
        var taskTitleNumber = String.valueOf(ThreadLocalRandom.current().nextInt(1000));
        taskEntity.setTitle("TASK_" + taskTitleNumber);
        taskEntity.setDescription(taskRequest.getDescription());
        taskEntity.setStatus(String.valueOf(TaskStatus.IN_PROGRESS));
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setCompletedAt(false);
        taskEntity.setUser(userRepository.findById(taskRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        MessageFormat.format(
                                "User with id {0} not found!", taskRequest.getUserId()))));
        var user = taskEntity.getUser();
        user.addTask(taskEntity);
        taskRepository.save(taskEntity);
        var event = new CreateEvent<TaskEntity>(taskEntity.getTitle(),true);
        var finalEvent = event.makeEvent(taskEntity,EVENT_NAME,taskEntity.getTitle());;
        eventPublisher.publishEvent(finalEvent);
        writeToRedis(taskEntity);
//        var mails = userRepository.findAll()
//                .stream()
//                .map(UserEntity::getEmail)
//                .filter(Objects::nonNull)
//                .toList();
//        emailService.sendEmail(mapToString(mails), user.getUsername(), event.toString());
        log.info("Event: {} is saved", finalEvent);
        return taskEntity;
    }



    @Override
    public TaskResponse findByTitle(String title) {
        return taskMapper.toDto(taskRepository.findByTitle(title)
                .orElseThrow(() -> new TaskNotFoundException(
                        MessageFormat.format("Task with title: {0} not found", title))));
    }

    @Override
    @Transactional
    public void update(Long id, final String status, Long userId) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        MessageFormat.format("Task with id: {0} not found", id)));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        MessageFormat.format("User with id {0} not found!", userId)));

        task.setUser(user);
        boolean isValidStatus = validStatus(status);
        if (isValidStatus) {
            task.setStatus(status.trim().toUpperCase());
            taskRepository.save(task);
            task.setUpdatedAt(LocalDateTime.now());
            var event = new UpdateEvent<TaskEntity>(task.getTitle(),true);
            var finalEvent = event.makeEvent(task,task.getTitle());
            eventPublisher.publishEvent(finalEvent);
            writeToRedis(task);
        } else {
            throw new RuntimeException("UNKNOWN STATUS");
        }

    }

    @Override
    @Transactional
    public void updateTaskList(List<Long> taskIds, String status, List<Long> userIds) {
        var tasks = userRepository.findAllById(userIds)
                .stream()
                .filter(Objects::nonNull)
                .flatMap(user -> user.getTasks().stream())
                .filter(task -> taskIds.contains(task.getId()))
                .distinct()
                .toList();

        tasks.forEach(task -> {
            task.setStatus(status);
        });
        var task = tasks.stream()
                .takeWhile(t->t.getTitle()!=null)
                .findFirst()
                .orElseThrow(RuntimeException::new);
        var event = new UpdateEvent<TaskEntity>(task.getTitle(),true,tasks);
        var finalEvent = event.makeEvent(tasks, task.getTitle());
        eventPublisher.publishEvent(finalEvent);
        taskRepository.saveAllAndFlush(tasks);
        log.info("UserTaskList is: {}", tasks);
    }




    @Override
    public void deleteById(final Long id) {
        taskRepository.findById(id)
                .ifPresentOrElse(taskEntity -> {
                    taskRepository.delete(taskEntity);
                    var event = new DeletedEvent<TaskEntity>(taskEntity.getTitle(),true);
                    var finalEvent = event.makeEvent(taskEntity,taskEntity.getTitle());
                    eventPublisher.publishEvent(finalEvent);
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
        List<String> taskTitles = Collections.singletonList(taskEntities.stream()
                .map(TaskEntity::getTitle)
                .map(String::trim)
                .toList()
                .stream()
                .toString());
        var event = new DeletedEvent<>(mapToString(taskTitles),true,taskEntities);
        var finalEvent = event.makeEvent(taskEntities,String.valueOf(taskTitles));
        eventPublisher.publishEvent(finalEvent);


    }

    private boolean validStatus(String status){
        return  status.equals(String.valueOf(TaskStatus.IN_PROGRESS)) ||
                status.equals(String.valueOf(TaskStatus.COMPLETED)) ||
                status.equals(String.valueOf(TaskStatus.EXPIRED)) ||
                status.equals(String.valueOf(TaskStatus.NEW));
    }



    private String mapToString(Object element){
        return String.valueOf(element);
    }


    private void writeToRedis(TaskEntity taskEntity){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            var key = mapToString(taskEntity.getId());
            var value = objectMapper.writeValueAsString(taskEntity);
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(mapToString(taskEntity.getId()),ttl.toMinutes(),TimeUnit.MINUTES);
        }  catch (JsonProcessingException e ){
            log.error("Error writing value in redis: {}",e.getLocalizedMessage());
            throw new RuntimeException();
        }
    }



}

