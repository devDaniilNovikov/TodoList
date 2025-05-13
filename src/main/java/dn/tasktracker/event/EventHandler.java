package dn.tasktracker.event;

import dn.tasktracker.aop.Loggable;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final TaskRepository taskRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final EmailService emailService;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @EventListener(TaskCreateEvent.class)
    @Loggable
    public void handle(final TaskCreateEvent taskCreateEvent) {
        log.info("Новое событие! Создана задача: {}", taskCreateEvent.toString());
            Map<String, List<TaskEntity>> taskMap = taskRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(TaskEntity::getTitle,
                            Collectors.filtering(
                                    TaskEntity::isExpired,
                                    Collectors.toList())));
            kafkaTemplate.send(topicName,taskMap);

            log.info("Отфильтрованна Корзина просроченных задач: {}", taskMap);
        }

        @EventListener(TaskUpdatedEvent.class)
        @Loggable
        public void handle(final TaskUpdatedEvent taskUpdatedEvent) {
        kafkaTemplate.send(topicName, taskUpdatedEvent);
        log.info("Новое событие! Обновлена задача: {}", taskUpdatedEvent.toString());
    }

    @EventListener(UserCreateEvent.class)
    @Loggable
    public void handle(final UserCreateEvent userCreateEvent) {
        kafkaTemplate.send(topicName, userCreateEvent);
        log.info("Новое событие! Создан пользователь: {}", userCreateEvent);
    }

}
