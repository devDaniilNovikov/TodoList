package dn.tasktracker.controller;
import dn.tasktracker.dto.ListTaskResponse;
import dn.tasktracker.dto.TaskRequest;
import dn.tasktracker.dto.TaskResponse;
import dn.tasktracker.dto.TaskSortDto;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private static final String GET_ALL_TASKS = "/api/v1/tasks/list";
    private static final String GET_TASK_WITH_PAGINATION = "/api/v1/tasks/sort";
    private static final String GET_TASK_BY_ID = "/api/v1/task/{id}";
    private static final String GET_TASK_BY_TITLE = "/api/v1/task/";
    private static final String CREATE_TASK = "/api/v1/task/create";
    private static final String UPDATE_TASK = "/api/v1/task/update/{id}";
    private static final String DELETE_TASK = "/api/v1/task/delete/{id}";
    private static final String SET_TIME_FOR_TASK = "/api/v1/tasks/{id}/?";
    private static final String ADD_USERS_FOR_TASK = "/api/v1/task/{id}/add-users";
    private static final String UPDATE_TASK_STATUS = "/api/v1/task/update/status/{id}";
    private static final String JSON_CONTENT_TYPE = "application/json";
    



    @GetMapping(value = GET_ALL_TASKS,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public ListTaskResponse findAll(){
        return taskService.getAll();
    }

    @PostMapping(value = ADD_USERS_FOR_TASK,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public Map<String,List<UserEntity>> addUsersForTask(@PathVariable Long id,
                                @RequestParam List<Long> userIds){
         return taskService.setUsersForTask(userIds,id);
    }



    @GetMapping(value = GET_TASK_WITH_PAGINATION,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public ListTaskResponse findAll(TaskSortDto taskDto){
        return taskService.getAll(taskDto);
    }

    @GetMapping(value = GET_TASK_BY_ID,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse findById(@PathVariable Long id){
        return taskService.getById(id);

    }

    @GetMapping(value = GET_TASK_BY_TITLE,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse findByTitle(@RequestParam String title){
        return taskService.findByTitle(title);
    }

    @PostMapping(value = CREATE_TASK,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskEntity createTask(@RequestBody TaskRequest taskRequest){
        return taskService.save(taskRequest);
    }

    @PatchMapping(value = UPDATE_TASK_STATUS)
    @ResponseStatus(HttpStatus.OK)
    public void updateTask(@PathVariable Long id,
                           @RequestParam String status,
                           @RequestParam Long userId){
        taskService.update(id, status,userId);
    }

    @DeleteMapping(DELETE_TASK)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id){
        taskService.deleteById(id);
    }




}
