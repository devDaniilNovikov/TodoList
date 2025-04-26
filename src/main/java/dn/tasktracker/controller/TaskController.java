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
    public static final String ADD_USERS_FOR_TASK = "/api/v1/task/{id}/add-users";



    @GetMapping(value = GET_ALL_TASKS)
    @ResponseStatus(HttpStatus.OK)
    public ListTaskResponse findAll(){
        return taskService.getAll();
    }

    @PostMapping(SET_TIME_FOR_TASK)
    @ResponseStatus(HttpStatus.OK)
    public Map<Long,List<TaskEntity>> setTimeForTask(@PathVariable("id") Long taskId,
                                                     @RequestParam Long userId,
                                                     @RequestParam Long time){
        return taskService.setTimeForTask(userId,taskId,time);
    }

    @PostMapping(ADD_USERS_FOR_TASK)
    @ResponseStatus(HttpStatus.OK)
    public Map<String,List<UserEntity>> addUsersForTask(@PathVariable Long id,
                                @RequestParam List<Long> userIds){
         return taskService.setUsersForTask(userIds,id);
    }



    @GetMapping(GET_TASK_WITH_PAGINATION)
    @ResponseStatus(HttpStatus.OK)
    public ListTaskResponse findAll(TaskSortDto taskDto){
        return taskService.getAll(taskDto);
    }

    @GetMapping(value = GET_TASK_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse findById(@PathVariable Long id){
        return taskService.getById(id);

    }

    @GetMapping(GET_TASK_BY_TITLE)
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse findByTitle(@RequestParam String title){
        return taskService.findByTitle(title);
    }

    @PostMapping(CREATE_TASK)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@RequestBody @Valid TaskRequest taskRequest){
        return taskService.save(taskRequest);
    }

    @PatchMapping(UPDATE_TASK)
    @ResponseStatus(HttpStatus.OK)
    public void updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest){
        taskService.update(id, taskRequest);
    }

    @DeleteMapping(DELETE_TASK)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id){
        taskService.deleteById(id);
    }




}
