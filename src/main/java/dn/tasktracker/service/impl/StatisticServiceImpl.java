package dn.tasktracker.service.impl;


import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.TaskStatus;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.entity.UserStatus;
import dn.tasktracker.exception.UserNotFoundException;
import dn.tasktracker.mapper.UserMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.StatisticService;
import dn.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class StatisticServiceImpl implements StatisticService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;


    @Override
    public Map<String, List<TaskEntity>> getUserStatisticByTasks(Long userId) {
        UserEntity user = userMapper.toEntity(userService.getById(userId));
        Map<String, List<TaskEntity>> userMap = Map.of(user.getUsername(), user.getTasks());
        long countOfFailedTasks = user.getTasks()
                .stream()
                .filter(task -> task.getStatus().equals(String.valueOf(TaskStatus.FAILED)))
                .count();
        if (countOfFailedTasks > 5) {
            userService.banAccount(userId);
            userRepository.save(user);
            log.info("Пользователь с идентификатором: {} забанен", userId);
            return Collections.emptyMap();
        }
        log.info("Пользователь с именем: {} имеет {} проваленных задач",
                user.getUsername(), countOfFailedTasks);


        return userMap;
    }


    @Override
    public Map<String, List<TaskEntity>> getUserStatisticByStatus(Long userId, String status) {
        UserEntity user = userMapper.toEntity(userService.getById(userId));
        Map<String,List<TaskEntity>> userMap = new HashMap<>();
        var mapValues = user.getTasks()
                .stream()
                .filter(task->task.getStatus().equals(status))
                .toList();
        userMap.put(user.getUsername(),mapValues);
        log.info("UserMap is: {}",userMap);
        return userMap;
    }

}
