package dn.tasktracker.web.controller;

import dn.tasktracker.web.CustomHttpHeaders;
import dn.tasktracker.web.dto.notifications.ListNotificationDto;
import dn.tasktracker.entity.NotificationEntity;
import dn.tasktracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private static final String SEND_NOTIFY = "/api/v1/notification/send";
    private static final String GET_NOTIFY_BY_ID = "/api/v1/notification/{id}";
    private static final String GET_ALL_NOTIFIES = "/api/v1/notifications/all";

    private final NotificationService notificationService;


    @PostMapping(SEND_NOTIFY)
    public void sendNotify(@RequestHeader(value = "OwnerId") Long ownerId,
                           @RequestParam Long userId,
                           @RequestParam String content){
        notificationService.sendNotification(ownerId,userId, content);
    }

    @GetMapping(GET_NOTIFY_BY_ID)
    public NotificationEntity getNotifyById(@PathVariable Long id){
        return notificationService.findById(id); //TODO: ДТО
    }

    @GetMapping(GET_ALL_NOTIFIES)
    public ListNotificationDto getAllNotifies(@RequestParam int pageNumber,
                                              @RequestParam int pageSize){
        return notificationService.getNotificationSet(pageNumber, pageSize);
    }
}
