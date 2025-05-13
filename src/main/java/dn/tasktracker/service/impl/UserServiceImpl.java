package dn.tasktracker.service.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import dn.tasktracker.dto.user.ListUserResponse;
import dn.tasktracker.dto.user.UserCreateRequest;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.entity.UserStatus;
import dn.tasktracker.event.UserCreateEvent;
import dn.tasktracker.exception.UserAlreadyExistsException;
import dn.tasktracker.exception.UserNotFoundException;
import dn.tasktracker.mapper.UserMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String USER_BY_ID_NOT_FOUND = "User with id {0} not found!";
    private static final String USER_BY_USERNAME_NOT_FOUND = "User with id {0} not found!";
    private static final String USER_BY_PHONE_NOT_FOUND = "User with id {0} not found!";
    private static final String TOPIC_NAME = "TaskTracker";


    @Override
    public List<UserResponse> findAllByIds(List<Long> ids) {
        return  userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .filter(Objects::nonNull)
                .toList();
    }


    @Override
    public List<UserEntity> findAll(Pageable pageable) {
        return userRepository.findAll(
                PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize())).getContent();
    }

    @Override
    public ListUserResponse findAllWithPagination(Pageable pageable) {
        var userEntities = userRepository.findAll(
                PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize())).getContent();
        var count = (long) userEntities.size();
        return new ListUserResponse(userEntities, (int) count);
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
        var event = new UserCreateEvent(
                user.getUsername(),
                user.getStatus(),
                user.getCreatedAt().format(formatter));
        eventPublisher.publishEvent(event);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponse getById(Long userId) {
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format(USER_BY_ID_NOT_FOUND, userId))));
    }

    @Override
    public UserResponse getByUsername(String username) {
        return userMapper.toDto(userRepository.getByUsername(username)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format(USER_BY_USERNAME_NOT_FOUND, username))));
    }

    @Override
    public UserResponse getByPhoneNumber(String phoneNumber) {
        return userMapper.toDto(userRepository.getByPhoneNumber(phoneNumber)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format(USER_BY_PHONE_NOT_FOUND, phoneNumber))));
    }



    @Override
    @Transactional
    public void banAccount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                        .orElseThrow(()->new UserNotFoundException(
                                MessageFormat.format(USER_BY_ID_NOT_FOUND, userId)));
        user.setStatus(String.valueOf(UserStatus.BANNED));
        userRepository.save(user);
        log.info("User {} is get ban! Actual status: {}",userId, user.getStatus());

    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
       UserEntity user = userMapper.toEntity(getById(userId));
       List<Long> taskIds = user.getTasks()
               .stream()
               .map(TaskEntity::getId)
               .filter(Objects::nonNull)
               .distinct()
               .toList();
       if (!taskIds.isEmpty()) {
           taskRepository.deleteAllByIdInBatch(taskIds);
       }
       log.info("Задачи пользователя: {} удалены.",user.getUsername());
       userRepository.delete(user);
       log.info("Пользователь {} удален.",user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword,
                               String newPassword,
                               Long userId) {
        UserEntity user = userMapper.toEntity(getById(userId));
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Пароль неверный, повторите попытку снова");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        log.info("Пароль пользователя {} изменен.", user.getUsername());
    }


    @Override
    @Transactional
    public void changeEmailForUser(String email, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format(USER_BY_ID_NOT_FOUND, userId)));
        user.setEmail(email);
        userRepository.save(user);
        log.info("User  {} email is changed to {}", user.getUsername(), email);
    }


}
