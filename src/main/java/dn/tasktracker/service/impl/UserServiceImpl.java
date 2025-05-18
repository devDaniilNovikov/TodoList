package dn.tasktracker.service.impl;
import dn.tasktracker.aop.Loggable;
import dn.tasktracker.dto.user.*;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.entity.UserStatus;
import dn.tasktracker.event.*;
import dn.tasktracker.exception.UserAlreadyExistsException;
import dn.tasktracker.exception.UserNotFoundException;
import dn.tasktracker.mapper.UserMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.EventService;
import dn.tasktracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final EventService eventService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String EVENT_NAME = "Creating of User";


    @Override
    public ListUserResponse findAllByIds(List<Long> ids) {
        return userMapper.toList(userRepository.findAllById(ids)
                .stream()
                .filter(Objects::nonNull)
                .toList());
    }


    @Override
    @Loggable
    public ListUserResponse findAll() {
        return userMapper.toList(userRepository.findAll());
    }

    @Override
    @Loggable
    public ListUserResponse findAllWithPagination(int pageNumber,int pageSize) {
        return userMapper.toList(userRepository.findAll(
                PageRequest.of(pageNumber,pageSize)).getContent());
    }


    @Override
    @Transactional
    public UserResponse createAccount(UserCreateRequest userCreateRequest) {
        UserEntity user = new UserEntity();
        user.setUsername(userCreateRequest.getUsername());
        if (userRepository.existsByUsername(userCreateRequest.getUsername())){
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(userCreateRequest.getPassword());
        user.setPhoneNumber(userCreateRequest.getPhoneNumber());
        user.setStatus(String.valueOf(UserStatus.ACTIVE));
        user.setEmail(userCreateRequest.getEmail());
        userRepository.save(user);
        var event = new CreateEvent<UserEntity>(user.getUsername(),true);
        var publishEvent = event.makeEvent(user,EVENT_NAME,user.getUsername());
        eventPublisher.publishEvent(publishEvent);
        return userMapper.toDto(user);
    }







    @Override
    public UserResponse getById(Long userId) {
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with id: {0} not found", userId))));
    }

    @Override
    public UserResponse getByUsername(String username) {
        return userMapper.toDto(userRepository.getByUsername(username)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with username: {0} not found", username))));
    }

    @Override
    public UserResponse getByPhoneNumber(String phoneNumber) {
        return userMapper.toDto(userRepository.getByPhoneNumber(phoneNumber)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with phoneNumber: {0} not found", phoneNumber))));
    }



    @Override
    @Transactional
    public void banAccount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                        .orElseThrow(()->new UserNotFoundException(
                                MessageFormat.format("User with id: {0} not found", userId)));
        user.setStatus(String.valueOf(UserStatus.BANNED));

        userRepository.save(user);
        log.info("User {} is get ban! Actual status: {}",userId, user.getStatus());

    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
       UserEntity user = userRepository.findById(userId)
               .orElseThrow(()->new UserNotFoundException(
                       MessageFormat.format("User with id: {0} not found", userId)));

        List<Long> taskIds = user.getTasks()
                .stream()
                .dropWhile(task->task.getId()!=null)
                .map(TaskEntity::getId)
                .toList();
       if (!taskIds.isEmpty()) {
           taskRepository.deleteAllByIdInBatch(taskIds);
       }
       var event = new DeletedEvent<UserEntity>(user.getUsername(),true);
       var finalEvent = event.makeEvent(user,user.getUsername());
       eventPublisher.publishEvent(finalEvent);
       redisTemplate.delete(String.valueOf(userId));
       log.info("Задачи пользователя: {} удалены.",user.getUsername());
       userRepository.delete(user);
       log.info("Пользователь {} удален.",user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(@Valid ChangePasswordDto changePasswordDto, Long userId) {
        UserEntity user = userMapper.toEntity(getById(userId));
        if (!user.getPassword().equals(changePasswordDto.getOldPassword())) {
            throw new IllegalArgumentException("Пароль неверный, повторите попытку снова");
        }
        user.setPassword(changePasswordDto.getNewPassword());
        userRepository.save(user);
        log.info("Пароль пользователя {} изменен.", user.getUsername());
    }


    @Override
    @Transactional
    public void changeEmailForUser(String email, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with id: {0} not found", userId)));
        user.setEmail(email);
        userRepository.save(user);
        var event = new UpdateEvent<UserEntity>(user.getUsername(),true);
        var finalEvent = event.makeEvent(user,EVENT_NAME,user.getUsername());
        eventPublisher.publishEvent(finalEvent);
        log.info("User  {} email is changed to {}", user.getUsername(), email);
    }

    @Override
    public void deleteAllByIds(List<Long> ids) {
            List<UserEntity> users = userRepository.findAllById(ids)
                    .stream()
                    .filter(Objects::nonNull)
                    .toList();
            List<Long> taskIds = users.stream()
                    .flatMap(user -> user.getTasks().stream())
                    .map(TaskEntity::getId)
                    .filter(Objects::nonNull)
                    .toList();
            List<TaskEntity> tasks = users.stream()
                    .map(UserEntity::getTasks)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .toList();
            List<String> taskTitles = tasks.stream()
                    .map(TaskEntity::getTitle)
                    .filter(t->!t.isBlank())
                    .toList();
            if (!taskIds.isEmpty()) {
                taskRepository.deleteAllByIdInBatch(taskIds);
            }
            var usernames = users.stream()
                    .map(UserEntity::getUsername)
                    .toList();
            var userEvent = new DeletedEvent<UserEntity>(String.valueOf(usernames), true, users);
            var taskEvent = new DeletedEvent<TaskEntity>(String.valueOf(taskIds),true,tasks);
            var finalTaskEvent = taskEvent.makeEvent(tasks,String.valueOf(taskTitles));
            var finalUserEvent = userEvent.makeEvent(users, usernames.toString());
            userRepository.deleteAllInBatch(users);
            eventPublisher.publishEvent(finalTaskEvent);
            eventPublisher.publishEvent(finalUserEvent);
            log.info("Deleted Tasks: {}",taskTitles);
            log.info("Deleted Users: {}", usernames);
        }


}
