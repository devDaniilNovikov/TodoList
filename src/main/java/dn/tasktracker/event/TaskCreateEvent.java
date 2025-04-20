package dn.tasktracker.event;

public record TaskCreateEvent(
        Long id,
        String title,
        String description,
        String status
) {

}
