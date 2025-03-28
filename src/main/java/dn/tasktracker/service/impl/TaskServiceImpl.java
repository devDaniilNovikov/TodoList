package dn.tasktracker.service.impl;
import dn.tasktracker.dto.TaskDto;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.event.TaskEvent;
import dn.tasktracker.exception.TaskNotFoundException;
import dn.tasktracker.mapper.TaskMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.TaskSpecification;
import dn.tasktracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@CacheConfig(cacheManager = "cacheManager")
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ApplicationEventPublisher eventPublisher;
    private static final String FAILED = "FAILED";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String COMPLETED = "COMPLETED";


    @Override
    @Cacheable(cacheNames = "taskList")
    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<TaskDto> findAll(TaskDto taskDto) {

        return taskMapper.toDtoList(taskRepository.findAll(
                TaskSpecification.withFilter(taskDto),
                PageRequest.of(taskDto.getPageNumber(),
                        taskDto.getPageSize())).getContent());
    }

    @Override
    public TaskDto getById(String id) {
        return taskMapper.toDto(taskRepository.findById(id)
                .orElseThrow(()->new TaskNotFoundException(
                        MessageFormat.format("Task with id: {0} not found",id)
                )));}

    @Override
    @Transactional
    @CacheEvict(cacheNames = "taskAfterCreate")
    public TaskDto save(TaskDto taskDto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskDto.getId());
        taskEntity.setTitle(taskDto.getTitle());
        taskEntity.setStatus(IN_PROGRESS);
        taskEntity.setDescription(taskDto.getDescription());
        taskEntity.setStatus(taskDto.getStatus());
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setCompletedAt(false);
        eventPublisher.publishEvent(
                new TaskEvent(
                        taskEntity.getId(),
                        taskEntity.getTitle(),
                        taskEntity.getDescription(),
                        taskEntity.getStatus()
                )
        );
        return taskMapper.toDto(taskRepository.save(taskEntity));
    }




    public void checkTaskStatus() {
        taskRepository.findAll()
                .stream()
                .filter(task -> task.getStatus().equals(FAILED))
                .peek(taskRepository::delete)
                .forEach(t-> {
                    log.info("Task  {} is deleted",t);
                });
    }

    public void checkTaskTime() {
        taskRepository.findAll()
                .stream()
                .filter(TaskEntity::isExpired)
                .map(task -> {
                    task.setCompletedAt(false);
                    task.setStatus(FAILED);
                    return taskRepository.save(task);
                })
                .map(TaskEntity::getId)
                .forEach(task -> {
                    log.info("Task with id: {} is failed", task);
    });
    }


    @Override
    public TaskDto findByTitle(String title) {
        return taskMapper.toDto(taskRepository.findByTitle(title)
                .orElseThrow(()->new TaskNotFoundException(
                        MessageFormat.format("Task with title {0} not found",title)
                )));
    }

    @Override
    public void changeStatus(String id, String status) {
        taskRepository.findById(id)
                .ifPresent(taskEntity -> {
                    taskEntity.setStatus(COMPLETED);
                    taskEntity.setCompletedAt(true);
                    taskRepository.save(taskEntity);
                    log.info("Status of task: {} is changed",taskEntity.getId());
                });
    }

    @Override
    @Transactional
    public void update(String id, TaskDto taskDto) {
         taskRepository.findById(id).ifPresentOrElse(task->{
                     task.setStatus(taskDto.getStatus());
                     task.setDescription(taskDto.getDescription());
                     task.setStatus(taskDto.getStatus());
                     task.setUpdatedAt(LocalDateTime.now());
                     taskRepository.save(task);
                 },  ()->  {
                     throw new TaskNotFoundException(
                         MessageFormat.format("Task with id: {0} not found",id));
                 });
    }

    @Override
    public void delete(String id) {
        taskRepository.findById(id)
                .ifPresentOrElse(taskEntity -> {
                    taskRepository.delete(taskEntity);
                    log.info("Deleted task: {}", taskEntity.getId());
                },()->{
                    throw new TaskNotFoundException(
                            MessageFormat.format("Task with id: {0} not found",id));
                });

    }

    @Override
    public void deleteAll(List<String> ids) {
        List<TaskEntity> taskEntities = taskRepository.findAllById(ids);
        taskEntities.forEach(taskEntity -> {
            taskRepository.deleteAllById(ids);
            log.info("Tasks for deleting task: {}", taskEntity.getId());
        });

    }



}

