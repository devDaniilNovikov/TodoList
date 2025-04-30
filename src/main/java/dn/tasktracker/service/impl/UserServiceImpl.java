package dn.tasktracker.service.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import dn.tasktracker.dto.user.UserCreateRequest;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.entity.UserStatus;
import dn.tasktracker.exception.UserNotFoundException;
import dn.tasktracker.mapper.UserMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public List<UserResponse> findAllByIds(List<Long> ids) {
        return  userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

//        if (ids.isEmpty()){
//            return Collections.emptyList();
//        }
//        List<UserEntity> users = userRepository.findAllById(
//                 ids.stream()
//                .filter(Objects::nonNull)
//                .distinct()
//                .toList());
//        try {
//            List<Long> userIds = users.stream()
//                    .map(UserEntity::getId)
//                    .toList();
//            List<UserResponseForRedis> userResponseForRedis = users.stream()
//                    .map(this::mapToRedisDto)
//                    .flatMap(Collection::stream)
//                    .toList();
//            var valueToRedis = objectMapper.writeValueAsString(userResponseForRedis);
//            redisTemplate.opsForValue().set(String.valueOf(userIds),valueToRedis);
//            return userMapper.toDtoList(users);
//        }catch (JsonProcessingException e){
//            throw new RuntimeException(e);
//         }
//
//    }

//    private  List<UserResponseForRedis> mapToRedisDto(UserEntity user){
//        UserResponseForRedis userResponseForRedis = new UserResponseForRedis();
//        userResponseForRedis.setId(user.getId());
//        userResponseForRedis.setUsername(user.getUsername());
//        userResponseForRedis.setPhoneNumber(user.getPhoneNumber());
//        userResponseForRedis.setStatus(user.getStatus());
//        userResponseForRedis.setCreatedAt(String.valueOf(user.getCreatedAt()));
//        userResponseForRedis.setUpdatedAt(String.valueOf(user.getUpdatedAt()));
//        return List.of(userResponseForRedis);
//    }


    @Override
    public List<UserEntity> findAll(Pageable pageable) {
        return userRepository.findAll(
                PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize())).getContent();
    }



    @Override
    @Transactional
    public UserResponse createAccount(UserCreateRequest userCreateRequest) {
        UserEntity user = new UserEntity();
        user.setUsername(userCreateRequest.getUsername());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(userCreateRequest.getPassword());
        user.setPhoneNumber(userCreateRequest.getPhoneNumber());
        user.setStatus(String.valueOf(UserStatus.ACTIVE));
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public UserResponse getById(Long userId) {
        log.info("Search of user with id: {}...",userId);
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with id {0} not found!", userId)
                )));
    }

    @Override
    public UserResponse getByUsername(String username) {
        return userMapper.toDto(userRepository.getByUsername(username)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with username {0} not found!", username)
                ))
        );
    }

    @Override
    public UserResponse getByPhoneNumber(String phoneNumber) {
        return userMapper.toDto(userRepository.getByPhoneNumber(phoneNumber)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with phone number {0} not found!", phoneNumber)
                )));
    }



    @Override
    @Transactional
    public void banAccount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                        .orElseThrow(()->new UserNotFoundException(
                                MessageFormat.format("User with id {0} not found!", userId)
                        ));
        user.setStatus(String.valueOf(UserStatus.BANNED));
        userRepository.save(user);
        log.info("User {} is get ban! Actual status: {}",userId, user.getStatus());

    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
       UserEntity user = userMapper.toEntity(getById(userId));
       List<Long> taskIds = user.getTasks().stream()
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
    public UserResponse setTasks(List<Long> taskIds, Long userId) {
        UserEntity user = userMapper.toEntity(getById(userId));
        Map<String,List<TaskEntity>> taskMap = new HashMap<>();
        var tasks = taskRepository.findAllById(taskIds)
                .stream()
                .filter(task -> !task.getUser().getId().equals(userId))
                .toList();
        user.setTasks(tasks);
        taskMap.put(String.valueOf(userId),user.getTasks());
        
        log.info("TaskMap is: {}",taskMap);
        userRepository.save(user);
        taskRepository.saveAll(tasks);
        log.info("Для пользователя {} добавлены задачи: {}",
                user.getUsername(),
                tasks.toArray());
        return userMapper.toDto(user);

    }



}
