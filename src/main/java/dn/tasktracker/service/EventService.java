package dn.tasktracker.service;

import dn.tasktracker.entity.Event;

import java.util.List;
import java.util.Map;

public interface EventService {

    Iterable<Event> findAllEvents();

    Map<String,List<Event>> findAllEventsByUserId(Long userId);

    Map<String,List<Event>> findAllEventsByTaskId(Long taskId);

    Event findEventById(Long id);

    Event findEventByName(String name);

    void createEvent();

    void deleteEvent();

    void updateEvent();

    void clearAllEvents(List<Event> events);



}
