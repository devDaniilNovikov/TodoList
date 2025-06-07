package dn.tasktracker.service;

import dn.tasktracker.entity.Event;
import dn.tasktracker.event.AbstractEvent;

import java.util.List;
import java.util.Map;

public interface EventService {

    Iterable<Event> findAllEvents();

    Map<String,List<Event>> findAllEventsOfUsersByUserId(Long userId);

    Map<String,List<Event>> findAllEventsOfTasksByTaskId(Long taskId);

    Event findEventById(Long id);

    Event findEventByName(String name);

    void createEvent();

    void deleteEvent();

    void updateEvent();

    void clearAllEvents(List<Event> events);

    default <T> AbstractEvent<?> makeEvent(T entity, String eventName){
        return AbstractEvent.<T>builder()
                .eventName(eventName)
                .deletedAt(true)
                .build();
    }



}
