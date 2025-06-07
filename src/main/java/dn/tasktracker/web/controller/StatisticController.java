package dn.tasktracker.web.controller;


import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "Statistic",description = "Действия со статистикой")
public class StatisticController {

    private static final String GET_USER_STAT_BY_TASKS = "/api/v1/stat/user/{id}";
    private static final String GET_USER_STAT_BY_TASK_STATUS = "/api/v1/stat/user/{id}/status/";
    private static final String GET_STATISTIC_OF_ALL_USERS_BY_IDS = "/api/v1/stat/users";
    private final StatisticService statisticService;

    @GetMapping(GET_USER_STAT_BY_TASKS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение статистики пользователя по всем его задачам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статисктика пользователя получена"),
            @ApiResponse(responseCode = "404",description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public Map<String,List<TaskEntity>> getUserStatByTasks(@PathVariable Long id) {
        return statisticService.getUserStatisticByTasks(id);
    }

    @GetMapping(GET_USER_STAT_BY_TASK_STATUS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение статусов задач пользователя по его уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статисктика пользователя получена"),
            @ApiResponse(responseCode = "400",description = "Некорректные данные"),
            @ApiResponse(responseCode = "404",description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public Map<String,List<TaskEntity>> getUserStatByTaskStatus(@PathVariable Long id,
                                                                @RequestParam String status) {
        return statisticService.getUserStatisticByStatus(id,status);
    }

    @GetMapping(GET_STATISTIC_OF_ALL_USERS_BY_IDS)
    @Operation(description = "Получение статистики всех пользователей по их уникальным идентификаторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика пользователь получена"),
            @ApiResponse(responseCode = "400",description = "Некорректные данные"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public Map<String ,List<TaskEntity>> getStatOfAllUsersByIds(@RequestParam Set<Long> ids){
        return statisticService.getUsersStatisticWithTasks(ids);
    }



}
