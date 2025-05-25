package dn.tasktracker.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dn.tasktracker.configuration.TopicNames;
import dn.tasktracker.service.RedisService;
import dn.tasktracker.web.CustomHttpHeaders;
import dn.tasktracker.web.dto.notifications.ListNotificationDto;
import dn.tasktracker.entity.NotificationEntity;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.web.dto.notifications.NotificationDto;
import dn.tasktracker.web.exception.BadRequestException;
import dn.tasktracker.web.exception.NotificationNotFoundException;
import dn.tasktracker.web.exception.TaskNotFoundException;
import dn.tasktracker.web.exception.UserNotFoundException;
import dn.tasktracker.web.mapper.NotificationMapper;
import dn.tasktracker.repository.NotificationRepository;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheManager = "redisCacheManager")
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final NotificationMapper notificationMapper;
    private final RedisService redisService;

    private static final int NOTIFICATION_CACHE_INDEX = 6;

    @Value("${spring.cache.cache-names}")
    private List<String> cacheNames;




    @Override
    @Cacheable(value = "NotificationEntity::getId",key = "#id")
    public NotificationEntity findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(()->new NotificationNotFoundException(
                        MessageFormat.format("Notification with id: {0} not found",id)
                ));
    }

    @Override
    @Transactional
    public void sendNotification(Long ownerId,Long userId, String content) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException(
                        MessageFormat.format("User with id: {0} not found",userId)));
        var notificationDto = createNotification(ownerId,content,user);
        log.info("Result of sending is: {}",notificationDto);
        var userWithHeaders = addToHeaders(ownerId);
        log.info("User id in headers: {}",userWithHeaders.getId());
        String jsonValue = mapToString(notificationDto.toString());
        redisService.writeInRedis(jsonValue,notificationDto.getId());
        String topic = TopicNames.TaskTracker.name();
        sendMessageToKafka(topic,jsonValue);



    }

    @Transactional
    @Override
    public void sendNotification(Long userId, Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(()->new TaskNotFoundException(
                        MessageFormat.format("Task with id: {0} not found",taskId)));
        boolean taskIsExpired = isExpired(task);
        if (taskIsExpired) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(
                            MessageFormat.format("User with id: {0} not found", userId)));

            String content = MessageFormat.format("Task {0} is expired!", task.getTitle());
            NotificationDto dto = createNotification(userId,content, user);
            log.info("Dto is: {}",dto);
            String jsonValue = mapToString(dto);
            redisService.writeInRedis(jsonValue,dto.getId());
            String topicName = TopicNames.TaskTracker.name();
            sendMessageToKafka(topicName, jsonValue);
        }

    }


    @Override
    @Transactional
    public void sendNotification(Long userId, File file) {

    }

    @Override
    public ListNotificationDto getNotificationSet(int pageNumber,
                                                  int pageSize) {
        return notificationMapper.toDtoWithNotificationList(
                notificationRepository.findAll(PageRequest.of(pageNumber,pageSize)).getContent()
        );
    }

    @Override
    public void sendBatchNotifications(Long ownerId, List<Long> userIds, Set<String> messages) {

    }



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

    private boolean isExpired(TaskEntity task){
        return ChronoUnit.MINUTES.between(task.getCreatedAt(),LocalDateTime.now())>1;
    }

    private NotificationDto createNotification(Long ownerId,
                                               String content,
                                               UserEntity user){
        NotificationEntity notification = new NotificationEntity();
        notification.setId(notification.getId());
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        user.addNotification(notification);
        notificationRepository.saveAndFlush(notification);
        NotificationDto notificationDto = new NotificationDto();
        var requireUser = addToHeaders(ownerId).getUsername();
        notificationDto.setId(notification.getId());
        notificationDto.setContent(notification.getContent());
        notificationDto.setFrom(requireUser);
        notificationDto.setTo(requireUser);
        notificationDto.setCreatedAt(notification.getCreatedAt());
        return notificationDto;

    }



//    private WeakReference<String> writeToRedis(int indexOfCache,String jsonValue){
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//            objectMapper.writeValueAsString(jsonValue);
//            String key = cacheNames.get(indexOfCache);
//            redisTemplate.opsForValue().set(key,jsonValue);
//            redisTemplate.expire(key,5,TimeUnit.MINUTES);
//            var result = Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString();
//            log.info("Successful writing in Redis: {}",result);
//            return new WeakReference<>(result);
//        }catch (JsonProcessingException e){
//            log.error("Cant write in Redis");
//            throw new RuntimeException();
//        }
//    }

    private void sendMessageToKafka(String topicName,
                                    String jsonValue){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValueAsString(jsonValue);
            kafkaTemplate.send(topicName,jsonValue);
            log.info("Successful sending of message to kafka");
        }catch (JsonProcessingException e){
            log.error("Cant send message to kafka");
        }
    }



    private String mapToString(Object element){
        return String.valueOf(element);
    }




}
