package dn.tasktracker.event;

import com.fasterxml.jackson.annotation.JsonFormat;

public record UserCreateEvent(String username,
                              String status,
                              String createdAt) {

}