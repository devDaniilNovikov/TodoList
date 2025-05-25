package dn.tasktracker.service;

import dn.tasktracker.web.dto.notifications.ListNotificationDto;
import dn.tasktracker.entity.NotificationEntity;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface NotificationService {


    NotificationEntity findById(Long id);

    void sendNotification(Long ownerId,
                          Long userId,
                          String message);

    void sendNotification(Long userId,Long taskId);

    void sendNotification(Long userId, File file);

    ListNotificationDto getNotificationSet(int pageNumber, int pageSize);

    void sendBatchNotifications(Long ownerId,
                                List<Long> userIds,
                                Set<String> messages);


}
