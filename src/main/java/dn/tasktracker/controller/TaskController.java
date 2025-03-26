package dn.tasktracker.controller;

import dn.tasktracker.dto.TaskDto;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private static final String GET_ALL_TASKS = "/api/v1/tasks/all";
    private static final String GET_TASK_WITH_PAGINATION = "/api/v1/tasks/sort";
    private static final String GET_TASK_BY_ID = "/api/v1/task/{id}";
    private static final String GET_TASK_BY_TITLE = "/api/v1/task/title/";
    private static final String CREATE_TASK = "/api/v1/task/create";
    private static final String UPDATE_TASK = "/api/v1/task/update/{id}";
    private static final String DELETE_TASK = "/api/v1/task/delete/{id}";

    @GetMapping(value = GET_ALL_TASKS)
    @ResponseStatus(HttpStatus.OK)
    public List<TaskEntity> findAll(){
        return taskService.findAll();
    }

    @GetMapping(GET_TASK_WITH_PAGINATION)
    public List<TaskDto> findAll(TaskDto taskDto){
        return taskService.findAll(taskDto);
    }

    @GetMapping(value = GET_TASK_BY_ID)
    public TaskDto findById(@PathVariable String id){
        return taskService.getById(id);

    }

    @GetMapping(GET_TASK_BY_TITLE)
    public TaskDto findByTitle(@RequestParam String title){
        return taskService.findByTitle(title);
    }

    @PostMapping(value = CREATE_TASK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskDto createTask(@RequestBody TaskDto taskDto){
        return taskService.save(taskDto);
    }

    @PutMapping(UPDATE_TASK)
    public void updateTask(@PathVariable String id,@RequestBody TaskDto taskDto){
        taskService.update(id,taskDto);
    }

    @DeleteMapping(DELETE_TASK)
    public void deleteTask(@PathVariable String id){
        taskService.delete(id);
    }



}
