package dn.tasktracker.service.impl;


import dn.tasktracker.entity.Event;
import dn.tasktracker.repository.EventRepository;
import dn.tasktracker.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Flow;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Map<String, List<Event>> findAllEventsOfUsersByUserId(Long userId) {
        return Map.of();
    }

    @Override
    public Map<String, List<Event>> findAllEventsOfTasksByTaskId(Long taskId) {
        return Map.of();
    }

    @Override
    public Event findEventById(Long id) {
        return null;
    }

    @Override
    public Event findEventByName(String name) {
        return null;
    }

    @Override
    public void createEvent() {

    }

    @Override
    public void deleteEvent() {

    }

    @Override
    public void updateEvent() {

    }

    @Override
    public void clearAllEvents(List<Event> events) {

    }
}
