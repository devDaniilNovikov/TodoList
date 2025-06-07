package dn.tasktracker.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dn.tasktracker.aop.Loggable;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.TaskStatus;
import dn.tasktracker.event.*;
import dn.tasktracker.service.RedisService;
import dn.tasktracker.web.exception.TaskNotFoundException;
import dn.tasktracker.web.exception.UserNotFoundException;
import dn.tasktracker.web.mapper.TaskMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.TaskSpecification;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.EmailService;
import dn.tasktracker.service.TaskService;
import dn.tasktracker.web.dto.ListTaskResponse;
import dn.tasktracker.web.dto.TaskRequest;
import dn.tasktracker.web.dto.TaskResponse;
import dn.tasktracker.web.dto.TaskSortDto;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.ThreadLocalRandom;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;
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
                }).toList()); //TODO: Сделать кэширование через RedisService
    }

    @Override
    public ListTaskResponse getAll() {
        return taskMapper.mapToDtoList(taskRepository.findAll()
                .stream()
                .peek(tasks -> {
                    redisTemplate.opsForList().leftPush(cacheNames.get(3), tasks.toString());
                    redisTemplate.expire(cacheNames.get(3), ttl.toMinutes(), TimeUnit.MINUTES);
                }).toList()); //TODO: Сделать кэширование через RedisService
    }


    @Override
    public TaskResponse getById(final Long id) {
        return taskMapper.toDto(taskRepository.findById(id)
                .stream()
                .peek(task -> {
                    redisService.writeInRedis(task,id);
                }).findAny()
                .orElseThrow(() -> new TaskNotFoundException(
                        MessageFormat.format("Task with id: {0} not found", id)
                )));

        //TODO: Сделать кэширование через RedisService
    }


    @Override
    @Transactional
    @Loggable
    public TaskResponse save(TaskRequest taskRequest) {
        TaskEntity taskEntity = new TaskEntity();
        ThreadLocalRandom.current();
        var taskTitleNumber = String.valueOf(
                ThreadLocalRandom.current()
                .nextInt(1000)
        );
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
        var isWriteToRedis = redisService.writeInRedis(taskEntity,taskEntity.getId()).get();
        log.info("Writing: {} in Redis",isWriteToRedis);
//        var mails = userRepository.findAll()
//                .stream()
//                .map(UserEntity::getEmail)
//                .filter(Objects::nonNull)
//                .toList();
//        emailService.sendEmail(mapToString(mails), user.getUsername(), event.toString());
        log.info("Event: {} is saved", finalEvent);
        return taskMapper.toDto(taskEntity);
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
            var redisValue = redisService.writeInRedis(task,task.getId()).get();
            log.info("Write: {} in Redis...",redisValue);
        } else {
            throw new RuntimeException("UNKNOWN STATUS");
        }

    }

    @Override
    @Transactional
    public void updateTaskList(Set<Long> taskIds, String status, Set<Long> userIds) {
        var tasks = userRepository.findAllById(userIds)
                .stream()
                .filter(Objects::nonNull)
                .flatMap(user -> user.getTasks().stream())
                .filter(task -> taskIds.contains(task.getId()))
                .collect(Collectors.toSet());

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
                    DeletedEvent<TaskEntity>  event = new DeletedEvent<TaskEntity>(taskEntity.getTitle(),true);
                    var finalEvent = event.makeEvent(taskEntity,taskEntity.getTitle());
                    eventPublisher.publishEvent(finalEvent);
                    log.info("Id of deleted task: {}", taskEntity.getId());
                },()->{
                    throw new TaskNotFoundException(
                            MessageFormat.format("Task with id: {0} not found",id));
                });

    }

    @Override
    public void deleteAllByIds(Set<Long> ids) {
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
        DeletedEvent<TaskEntity> event = new DeletedEvent<>(mapToString(taskTitles), true,taskEntities);
        var finalEvent = event.makeEvent(taskEntities,mapToString(taskTitles));
        eventPublisher.publishEvent(finalEvent);


    }

    private boolean validStatus(String status) {
        return switch (status) {
            case "IN_PROGRESS", "COMPLETED", "EXPIRED", "NEW" -> true;
            default -> false;
        };
    }



    private String mapToString(Object element){
        return String.valueOf(element);
    }
    
}

