package dn.tasktracker.event;

import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final TaskRepository taskRepository;

    @EventListener(TaskCreateEvent.class)
    public void handle(final TaskCreateEvent taskCreateEvent) {
        log.info("New Event! Task is created: {}", taskCreateEvent.toString());
            Map<Long, List<TaskEntity>> taskMap = taskRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(TaskEntity::getId,
                            Collectors.filtering(
                                    TaskEntity::isExpired,
                                    Collectors.toList())));
            log.info("Task Map: {}", taskMap);
        }



    @EventListener(TaskUpdatedEvent.class)
    public void handle(final TaskUpdatedEvent taskUpdatedEvent) {
        log.info("New Event! Task status was updated: {}", taskUpdatedEvent.toString());
    }

    @EventListener
    public void handle(final UserCreateEvent userCreateEvent) {
        log.info("New Event! User is created: {}", userCreateEvent.toString());
    }

}
