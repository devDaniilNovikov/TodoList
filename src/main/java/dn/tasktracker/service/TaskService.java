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

    Map<String,List<UserEntity>> save(TaskRequest taskRequest, List<Long> userIds);

    Map<String,List<UserEntity>> setUsersForTask(List<Long> userIds, Long taskId);

    TaskResponse findByTitle(String title);

    Map<Long,List<TaskEntity>> setTimeForTask(Long userId, Long taskId, Long time);

    void update(Long id, TaskRequest taskRequest);

    void deleteById(Long id);

    void deleteAllByIds(List<Long> ids);


}
