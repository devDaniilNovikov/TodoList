package dn.tasktracker.service;

import dn.tasktracker.dto.TaskDto;
import dn.tasktracker.entity.TaskEntity;
import org.springframework.data.redis.stream.Task;

import java.util.List;

public interface TaskService {

    List<TaskEntity> findAll();

    List<TaskDto> findAll(TaskDto taskDto);

    TaskDto getById(String id);

    TaskDto save(TaskDto taskDto);

    TaskDto findByTitle(String title);

    void changeStatus(String id,String status);

    void update(String id,TaskDto taskDto);

    void delete(String id);

    void deleteAll(List<String> ids);


}
