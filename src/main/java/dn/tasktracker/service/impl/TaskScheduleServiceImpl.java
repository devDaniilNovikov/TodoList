package dn.tasktracker.service.impl;

import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.service.TaskScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final RedisTemplate<String,Object> redisTemplate;
    private static final String FAILED = "FAILED";
    private static final Duration cacheTtl = Duration.ofMinutes(10);


    @SneakyThrows
    @Transactional
    public void checkStatus() {
        Thread.startVirtualThread(() -> {
            Map<String, List<TaskEntity>> tasksByStatus = taskRepository.findAll()
                    .stream()
                    .filter(TaskEntity::isFailed)
                    .collect(Collectors.groupingBy(
                            TaskEntity::getStatus,
                            Collectors.filtering(TaskEntity::isExpired,
                                    Collectors.toList())));

            log.info("TaskMap is: {}", tasksByStatus);
            List<TaskEntity> failedTasks = tasksByStatus
                    .getOrDefault(FAILED.trim(),
                            Collections.emptyList());
            log.info("Failed tasks: {}", failedTasks);
            Map<String, Long> tasksCountByStatus = failedTasks.stream()
                    .collect(Collectors.groupingBy(
                            TaskEntity::getStatus,
                            Collectors.counting()));
            log.info("Count of failed tasks is: {}", tasksCountByStatus);
            List<Long> taskIds = failedTasks.stream()
                    .map(TaskEntity::getId)
                    .collect(Collectors.toList());
            log.info("Id's of failed tasks is: {}", taskIds);
            log.info("Tasks count by status: {}", tasksCountByStatus);
            log.info("Ids of tasks for delete: {}", Arrays.toString(taskIds.toArray()));

            if (!taskIds.isEmpty()) {
                redisTemplate.delete(taskIds.toString());
                taskRepository.deleteAllInBatch(failedTasks);
                log.info("Deleted failed tasks: {}", taskIds);
            }
        }).join(1);
    }

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
    public void cleanCache(){
        redisTemplate.delete(redisTemplate.keys("*"));
        log.info("Cache is cleaned");
    }





    @Override
    public void checkTaskTime() {
        taskRepository.findAll()
                .stream()
                .filter(TaskEntity::isExpired)
                .map(task -> {
                    task.setCompletedAt(false);
                    task.setStatus(FAILED);
                    redisTemplate.opsForValue().set(task.toString(), cacheTtl, task.getId());
                    return taskRepository.save(task);
                })
                .map(TaskEntity::getId)
                .forEach(task -> {
                    log.info("Task with id: {} is failed", task);
                });
    }
}
