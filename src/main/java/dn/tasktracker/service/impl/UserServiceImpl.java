package dn.tasktracker.service.impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dn.tasktracker.aop.Loggable;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.entity.UserStatus;
import dn.tasktracker.event.*;
import dn.tasktracker.web.CustomHttpHeaders;
import dn.tasktracker.web.exception.UserAlreadyExistsException;
import dn.tasktracker.web.exception.UserNotFoundException;
import dn.tasktracker.web.mapper.UserMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.UserService;
import dn.tasktracker.web.dto.user.ChangePasswordDto;
import dn.tasktracker.web.dto.user.ListUserResponse;
import dn.tasktracker.web.dto.user.UserCreateRequest;
import dn.tasktracker.web.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public  class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private static final String EVENT_NAME = "Creating of User";
    @Value("${spring.cache.cache-names}")
    private List<String> cacheNames;


    @Override
    public ListUserResponse findAllUsersByIds(List<Long> userIds) {
        if (userIds.isEmpty()){
            throw new IllegalArgumentException("List of ids can't be empty");
        }
        return userMapper.toList(userRepository.findAllById(userIds)
                .stream()
                .filter(Objects::nonNull)
                .toList());
    }

    @Override
    public ListUserResponse findAllByUsersTasksIds(List<Long> taskIds) {
        if (taskIds.isEmpty()){
            throw new IllegalArgumentException("List of ids can't be empty");
        }
        return userMapper.toList(userRepository.findAllByTasksIds(taskIds)
                .stream()
                .filter(Objects::nonNull)
                .peek(this::writeToRedis)
                .toList());
    }

    private void writeToRedis(UserEntity user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setDateFormat(DateFormat.getDateInstance());
            var key = cacheNames.get(5);
            var value = objectMapper.writeValueAsString(user);
            redisTemplate.opsForList().leftPush(key, value);
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
            log.info("Value write to redis: key= {}, value= {}",key,value);
        } catch (JsonProcessingException e) {
            log.info("JsonProcessingException is: {}", e.getLocalizedMessage());
        }

    }

    @Override
    @Loggable
    public ListUserResponse findAllUsers() {
        return userMapper.toList(userRepository.findAll());
    }

    @Override
    @Loggable
    public ListUserResponse findAllUsersWithPagination(int pageNumber, int pageSize) {
        return userMapper.toList(userRepository.findAll(
                PageRequest.of(pageNumber, pageSize))
                .getContent());
    }


    @Override
    @Transactional
    @Loggable
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
        var event = new CreateEvent<UserEntity>(user.getUsername());
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
    public UserResponse getByTaskTitle(String taskTitle) {
        return userMapper.toDto(userRepository.findByTaskTitle(taskTitle)
                .stream()
                .map(UserEntity::getTasks)
                .flatMap(Collection::stream)
                .takeWhile(task->task.getTitle().equals(taskTitle))
                .map(TaskEntity::getUser)
                .findAny()
                .orElseThrow(()->new UserNotFoundException("User not found")));
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
    public UserEntity addToHeaders(Long ownerId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        var user = userRepository.findById(ownerId)
                .map(it -> {
                    String headerName = CustomHttpHeaders.OWNER_ID.name();
                    String ownerIdStr = mapToString(it.getId());
                    httpHeaders.add(headerName, ownerIdStr);
                    httpHeaders.set(headerName, ownerIdStr);
                    return it;
                }).stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
        var headerValue = Collections.singletonList(mapToString(user.getId()));
        httpHeaders.put(CustomHttpHeaders.OWNER_ID.name(), headerValue);
        log.info("Http headers is: {}", httpHeaders);
        return user;
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
    public void changePassword(ChangePasswordDto changePasswordDto, Long userId) {
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
    @Transactional
    public void deleteAllUsersByIds(Set<Long> userIds) {
        if (userIds.isEmpty()){
            throw new IllegalArgumentException("List of ids can't be empty");
        }
            List<UserEntity> users = userRepository.findAllById(userIds)
                    .stream()
                    .filter(Objects::nonNull)
                    .toList();
            List<Long> taskIds = users.stream()
                    .map(UserEntity::getTasks)
                    .flatMap(Collection::stream)
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
                    .filter(username->!username.isEmpty())
                    .filter(username->!username.isBlank())
                    .map(String::trim)
                    .toList();
            DeletedEvent<UserEntity> userEvent = new DeletedEvent<>(mapToString(usernames), true, users);
            DeletedEvent<TaskEntity> taskEvent = new DeletedEvent<>(mapToString(taskIds), true, tasks);
            var finalTaskEvent = taskEvent.makeEvent(tasks,mapToString(taskTitles));
            var finalUserEvent = userEvent.makeEvent(users, mapToString(usernames));
            userRepository.deleteAllInBatch(users);
            eventPublisher.publishEvent(finalTaskEvent);
            eventPublisher.publishEvent(finalUserEvent);
            log.info("Deleted Tasks: {}",taskTitles);
            log.info("Deleted Users: {}", usernames);
        }

    private String mapToString(Object element){
        return String.valueOf(element);
    }


}
