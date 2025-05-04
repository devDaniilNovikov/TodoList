package dn.tasktracker.event;

import lombok.NoArgsConstructor;

public record TaskCreateEvent(
        Long id,
        String title,
        String description,
        String status,
        String worker){


}
