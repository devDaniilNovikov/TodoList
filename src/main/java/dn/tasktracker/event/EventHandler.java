package dn.tasktracker.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dn.tasktracker.aop.Loggable;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EmailService emailService;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

//    @EventListener(Object.class)
//    @Loggable
//    public void handleCreateEvent(final TaskCreateEvent taskCreateEvent) {
//        log.info("Новое событие! Создана задача: {}", taskCreateEvent.toString());
//        Map<String, List<TaskEntity>> taskMap = taskRepository.findAll()
//                .stream()
//                .collect(Collectors.groupingBy(TaskEntity::getTitle,
//                        Collectors.filtering(
//                                TaskEntity::isExpired,
//                                Collectors.toList())));
//        kafkaTemplate.send(topicName, taskMap);
//
//        log.info("Отфильтрованная Корзина просроченных задач: {}", taskMap);
//    }
//
//    @EventListener(Object.class)
//    @Loggable
//    public void handleUpdateEvent(final TaskUpdateEvent taskUpdatedEvent) {
//        List<TaskEntity> filteredTasks = taskRepository.findAll()
//                .stream()
//                .filter(taskEntity->taskEntity.getStatus()
//                        .equalsIgnoreCase(taskUpdatedEvent.getStatus()))
//                .toList();
//        Map<String,List<TaskEntity>> listMap = filteredTasks.stream()
//                .collect(Collectors.groupingBy(TaskEntity::getTitle));
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//            var message = objectMapper.writeValueAsString(listMap);
//            kafkaTemplate.send(topicName, message);
//        }catch (JsonProcessingException e){
//            log.info("Can't write message as string");
//            throw new RuntimeException(e);
//        }
//
//        log.info("Корзина задач: {}",listMap);
//        log.info("Новое событие! Обновлена задача: {}", taskUpdatedEvent);
//    }


    @EventListener(DeletedEvent.class)
    @Async
    public void handleDeleteEvent(DeletedEvent<?> event){
        log.info("Deleted event is: {}",event.getEventName());
    }

    @EventListener(CreateEvent.class)
    public void handleCreateEvent(CreateEvent<?> event) {
           log.info("Created event is: {}, Data is: {}", event.getEventName(), event.getData());
            taskRepository.findByTitle(event.getData())
                    .ifPresentOrElse(
                    task -> {
                        kafkaTemplate.send(topicName, task);
                        log.info("Message sent for task topic");
                    }, () -> userRepository.getByUsername(event.getData())
                            .ifPresent(user -> {
                        kafkaTemplate.send(topicName, user);
                        log.info("Message sent for user topic: {}", user);
                    }));
        }


    @EventListener(UpdateEvent.class)
    @Async
    public void handleUpdatingEvent(UpdateEvent<?> event){
         log.info("UpdatedEvent is: {}", event.getElements());
    }




}






