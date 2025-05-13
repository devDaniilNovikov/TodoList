package dn.tasktracker.controller;
import dn.tasktracker.dto.*;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.service.ExportService;
import dn.tasktracker.service.TaskService;
import io.lettuce.core.SslOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Task",description = "Действия с задачами")
public class TaskController {

    private final TaskService taskService;
    private final ExportService exportService;
    private static final String GET_ALL_TASKS = "/api/v1/tasks/list";
    private static final String GET_TASK_WITH_PAGINATION = "/api/v1/tasks/sort";
    private static final String GET_TASK_BY_ID = "/api/v1/task/{id}";
    private static final String GET_TASK_BY_TITLE = "/api/v1/task/";
    private static final String CREATE_TASK = "/api/v1/task/create";
    private static final String DELETE_TASK = "/api/v1/task/delete/{id}";
    private static final String UPDATE_TASK_STATUS = "/api/v1/task/update/status/{id}";
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String DOWNLOAD_EXCEL_FILE = "/api/v1/task/excel/download";
    



    @GetMapping(value = GET_ALL_TASKS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение списка всех задач")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список всех задач получен"),
            @ApiResponse(responseCode = "404",description = "Задачи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public ListTaskResponse findAll(){
        return taskService.getAll();
    }


    @GetMapping(value = GET_TASK_WITH_PAGINATION,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение списка задач постранично")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи получены"),
            @ApiResponse(responseCode = "404",description = "Задачи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public ListTaskResponse findAll(TaskSortDto taskDto){
        return taskService.getAll(taskDto);
    }

    @GetMapping(value = GET_TASK_BY_ID,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение задачи по уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача получена"),
            @ApiResponse(responseCode = "404",description = "Задачи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public TaskResponse findById(@PathVariable Long id){
        return taskService.getById(id);

    }

    @GetMapping(value = GET_TASK_BY_TITLE,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение задачи по заголовку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача получена"),
            @ApiResponse(responseCode = "404",description = "Задачи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public TaskResponse findByTitle(@RequestParam String title){
        return taskService.findByTitle(title);
    }

    @PostMapping(value = CREATE_TASK,consumes = JSON_CONTENT_TYPE,produces = JSON_CONTENT_TYPE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Создание задачи")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача создана"),
            @ApiResponse(responseCode = "400",description = "Задача не создана, некорректные данные"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public TaskEntity createTask(@RequestBody TaskRequest taskRequest){
        return taskService.save(taskRequest);
    }

    @PatchMapping(value = UPDATE_TASK_STATUS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Обновление задачи по уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача получена"),
            @ApiResponse(responseCode = "400",description = "Задача не обновлена, некорректные данные"),
            @ApiResponse(responseCode = "404",description = "Задача не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public void updateTask(@PathVariable Long id,
                           @RequestParam String status,
                           @RequestParam Long userId){
        taskService.update(id, status,userId);
    }

    @DeleteMapping(DELETE_TASK)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Удаление задачи по уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача удалена"),
            @ApiResponse(responseCode = "404",description = "Задача не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public void deleteTask(@PathVariable Long id){
        taskService.deleteById(id);
    }

    @GetMapping(DOWNLOAD_EXCEL_FILE)
    @SneakyThrows
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> exportTask(){
//        String filename = "tasks_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")) + ".xlsx";
//        String filePath = System.getProperty("java.io.tmpdir") + "/" + filename;
//        exportService.exportToExcelFile(filePath);
//        ByteArrayResource byteArrayResource = new ByteArrayResource(Files.readAllBytes(Paths.get(filePath)));
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + filename)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(byteArrayResource);
        return null;
    }




}
