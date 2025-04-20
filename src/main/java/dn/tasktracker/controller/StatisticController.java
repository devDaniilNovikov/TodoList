package dn.tasktracker.controller;


import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatisticController {

    private static final String GET_USER_STAT_BY_TASKS = "/api/v1/stat/user/{id}";
    private static final String GET_USER_STAT_BY_TASK_STATUS = "/api/v1/stat/user/{id}/status/";
    private final StatisticService statisticService;

    @GetMapping(GET_USER_STAT_BY_TASKS)
    @ResponseStatus(HttpStatus.OK)
    public Map<String,List<TaskEntity>> getUserStatByTasks(@PathVariable Long id) {
        return statisticService.getUserStatisticByTasks(id);
    }

    @GetMapping(GET_USER_STAT_BY_TASK_STATUS)
    @ResponseStatus(HttpStatus.OK)
    public Map<String,List<TaskEntity>> getUserStatByTaskStatus(@PathVariable Long id,
                                                               @RequestParam String status) {
        return statisticService.getUserStatisticByStatus(id,status);
    }



}
