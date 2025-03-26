package dn.tasktracker.service.impl;
import dn.tasktracker.dto.TaskDto;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.exception.TaskNotFoundException;
import dn.tasktracker.mapper.TaskMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.TaskSpecification;
import dn.tasktracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private static final Logger LOGGER = Logger.getLogger(TaskServiceImpl.class.getName());
    private final TaskMapper taskMapper;

    @Override
    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<TaskDto> findAll(TaskDto taskDto) {
        return taskMapper.toDtoList(taskRepository.findAll(TaskSpecification.withFilter(taskDto),
                PageRequest.of(taskDto.getPageNumber(), taskDto.getPageSize())).getContent());
    }

    @Override
    public TaskDto getById(String id) {
        return taskMapper.toDto(taskRepository.findById(id)
                .orElseThrow(()->new TaskNotFoundException(
                        MessageFormat.format("Task with id: {0} not found",id)
                )));}

    @Override
    public TaskDto save(TaskDto taskDto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskEntity.getId());
        taskEntity.setTitle(taskDto.getTitle());
        taskEntity.setStatus(taskDto.getStatus());
        taskEntity.setDescription(taskDto.getDescription());
        taskEntity.setStatus(taskDto.getStatus());
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setCompletedAt(false);
        LOGGER.info(taskDto.threadLocalRandom().toString());
        LOGGER.info("Creating of task: {}");
        return taskMapper.toDto(taskRepository.save(taskEntity));
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
                    taskEntity.setStatus(status.toUpperCase());
                    taskEntity.setCompletedAt(true);
                    taskRepository.save(taskEntity);
                    log.info("Status of task: {} is changed",taskEntity.getId());
                });
    }

    @Override
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

