package dn.tasktracker.service.impl;

import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.TaskStatus;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.service.TaskScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.stats.StatisticsRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskScheduleServiceImpl implements TaskScheduleService {

    private final TaskRepository taskRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FAILED = "FAILED";
    private static final Duration cacheTtl = Duration.ofMinutes(10);


    //    @Scheduled(fixedRate = 1000)
    @Override
    @Transactional
    public void checkTaskStatus() {
        List<TaskEntity> tasksForDelete = taskRepository.findAll()
                .stream()
                .filter(task -> task.getStatus().equals(FAILED.trim()))
                .filter(TaskEntity::isExpired)
                .toList();
        List<Long> taskIds = tasksForDelete.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .keySet()
                .stream()
                .toList();
        log.info("Ids of tasks for delete : {} ", Arrays.toString(taskIds.toArray()));
        redisTemplate.delete(String.valueOf(taskIds));

        log.info("List of tasks for delete: {}", Arrays.toString(tasksForDelete.toArray()));
        taskRepository.deleteAllInBatch(tasksForDelete);
        log.info("Tasks for delete: {}", taskIds.toArray());

    }


    @Scheduled(cron = "0 * */12 * * ?")
    public void cleanCache() {
        redisTemplate.delete(redisTemplate.keys("*"));
        log.info("Cache is cleaned");

    }


//    @Scheduled(fixedDelay = 5000)
    @Override
    @Transactional
    public void checkTaskTime() {
        var taskExpiredList = taskRepository.findAll()
                .stream()
                .filter(TaskEntity::isExpired)
                .peek(task -> {
                    task.setCompletedAt(false);
                    task.setStatus(String.valueOf(TaskStatus.FAILED));
                    var key = String.valueOf(task.getId());
                    var value = String.valueOf(task);
                    redisTemplate.opsForValue().set(key, value, cacheTtl);
                    log.info("Task {} is written to redis", task);
                })
                .map(taskRepository::save)
                .map(TaskEntity::getId)
                .toList();
        var requiredTasks = taskRepository.findAllById(taskExpiredList);
        Map<String, List<TaskEntity>> taskMap = taskRepository.findAll()
                .stream()
                .filter(task -> TaskStatus.FAILED.name().equals(task.getStatus()))
                .collect(Collectors.groupingBy(task -> String.valueOf(task.getUser().getId())));
        log.info("Task Map: {}", taskMap);
        taskMap.keySet()
                .stream()
                .filter(Objects::nonNull)
                .forEach(task->{
                    taskRepository.deleteAllInBatch(requiredTasks);
                    redisTemplate.delete(List.of(requiredTasks).toString());
                });

        log.info("Map for delete: {}", taskMap);
    }


}

