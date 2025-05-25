package dn.tasktracker.service;

import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.web.dto.ListTaskResponse;
import dn.tasktracker.web.dto.TaskRequest;
import dn.tasktracker.web.dto.TaskResponse;
import dn.tasktracker.web.dto.TaskSortDto;

import java.util.Set;

public interface TaskService {


    ListTaskResponse getAll(TaskSortDto taskDto);

    ListTaskResponse getAll();

    TaskResponse getById(Long id);

    TaskEntity save(TaskRequest taskRequest);

    TaskResponse findByTitle(String title);

    void update(Long id,String status,Long userId);

    void updateTaskList(Set<Long> taskIds, String status, Set<Long> userIds);

    void deleteById(Long id);

   void deleteAllByIds(Set<Long> ids);


}
