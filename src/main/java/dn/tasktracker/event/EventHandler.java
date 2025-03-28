package dn.tasktracker.event;

import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final TaskRepository taskRepository;

    @EventListener(TaskEvent.class)
    public void handle(final TaskEvent taskEvent) {
        log.info("Handling TaskEvent: {}", taskEvent);
        if (taskEvent.status().equals("IN_PROGRESS")) {
            for (TaskEntity task : taskRepository.findAll()) {
                if (task.isExpired()) {
                    taskRepository.delete(task);
                }
            }
        }
    }
}
