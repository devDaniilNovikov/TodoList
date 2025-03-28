package dn.tasktracker.event;

public record TaskEvent(
        String id,
        String title,
        String description,
        String status
) {

}
