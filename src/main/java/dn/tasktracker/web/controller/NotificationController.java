package dn.tasktracker.web.controller;

import dn.tasktracker.web.CustomHttpHeaders;
import dn.tasktracker.web.dto.notifications.ListNotificationDto;
import dn.tasktracker.entity.NotificationEntity;
import dn.tasktracker.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notification",description = "Дейтсвия с уведомлениями")
public class NotificationController {

    private static final String SEND_NOTIFY = "/api/v1/notification/send";
    private static final String GET_NOTIFY_BY_ID = "/api/v1/notification/{id}";
    private static final String GET_ALL_NOTIFIES = "/api/v1/notifications/all";

    private final NotificationService notificationService;


    @PostMapping(SEND_NOTIFY)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Отправка уведомления")
    @ApiResponses ({
        @ApiResponse(responseCode = "200",description = "Уведомление успешно отправлено"),
        @ApiResponse(responseCode = "400",description = "Некорректные данные"),
        @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public void sendNotify(@RequestHeader(value = "OwnerId") Long ownerId,
                           @RequestParam Long userId,
                           @RequestParam String content){
        notificationService.sendNotification(ownerId,userId, content);
    }

    @GetMapping(GET_NOTIFY_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение уведомления по его уникальному идентификатору")
    @ApiResponses ({
            @ApiResponse(responseCode = "200",description = "Уведомление успешно получено"),
            @ApiResponse(responseCode = "404",description = "Уведомление не найдено"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public NotificationEntity getNotifyById(@PathVariable Long id){
        return notificationService.findById(id); //TODO: ДТО
    }

    @GetMapping(GET_ALL_NOTIFIES)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение списка уведомлений с пагинацией")
    @ApiResponses ({
            @ApiResponse(responseCode = "200",description = "Уведомление успешно отправлено"),
            @ApiResponse(responseCode = "400",description = "Некорректные данные"),
            @ApiResponse(responseCode = "404",description = "Уведомления не найдено"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public ListNotificationDto getAllNotifies(@RequestParam int pageNumber,
                                              @RequestParam int pageSize){
        return notificationService.getNotificationSet(pageNumber, pageSize);
    }
}
