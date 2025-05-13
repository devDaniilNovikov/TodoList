package dn.tasktracker.controller;


import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Statistic",description = "Действия со статистикой")
public class StatisticController {

    private static final String GET_USER_STAT_BY_TASKS = "/api/v1/stat/user/{id}";
    private static final String GET_USER_STAT_BY_TASK_STATUS = "/api/v1/stat/user/{id}/status/";
    private final StatisticService statisticService;

    @GetMapping(GET_USER_STAT_BY_TASKS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение статистики пользовалея всем задачам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача удалена"),
            @ApiResponse(responseCode = "404",description = "Задача не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public Map<String,List<TaskEntity>> getUserStatByTasks(@PathVariable Long id) {
        return statisticService.getUserStatisticByTasks(id);
    }

    @GetMapping(GET_USER_STAT_BY_TASK_STATUS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение статусов по задачам пользователя по его уникальному идентификатору")
    public Map<String,List<TaskEntity>> getUserStatByTaskStatus(@PathVariable Long id,
                                                                @RequestParam String status) {
        return statisticService.getUserStatisticByStatus(id,status);
    }



}
