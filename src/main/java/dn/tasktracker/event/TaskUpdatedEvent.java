package dn.tasktracker.event;

import java.time.LocalDateTime;

public record TaskUpdatedEvent(
        Long id,
        String title,
        String description,
        String status,
        LocalDateTime updatedAt
) {
}
