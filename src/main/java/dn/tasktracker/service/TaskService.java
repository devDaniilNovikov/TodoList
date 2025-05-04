package dn.tasktracker.service;

import dn.tasktracker.dto.ListTaskResponse;
import dn.tasktracker.dto.TaskRequest;
import dn.tasktracker.dto.TaskResponse;
import dn.tasktracker.dto.TaskSortDto;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface TaskService {


    ListTaskResponse getAll(TaskSortDto taskDto);

    ListTaskResponse getAll();

    TaskResponse getById(Long id);

    TaskEntity save(TaskRequest taskRequest);

    TaskResponse findByTitle(String title);

    void update(Long id,String status,Long userId);

    void deleteById(Long id);

    void deleteAllByIds(List<Long> ids);


}
