package dn.tasktracker.service;

import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StatisticService {

    Map<String, List<TaskEntity>> getUserStatisticByTasks(Long userId);

    Map<String, List<TaskEntity>> getUserStatisticByStatus(Long userId, String status);

    Map<String,List<TaskEntity>> getUsersStatisticWithTasks(Set<Long> userIds);



}
