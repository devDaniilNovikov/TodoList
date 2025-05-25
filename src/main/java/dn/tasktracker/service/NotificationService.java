package dn.tasktracker.service;

import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.web.dto.notifications.ListNotificationDto;
import dn.tasktracker.entity.NotificationEntity;
import dn.tasktracker.web.dto.notifications.NotificationRequest;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface NotificationService {


    NotificationEntity findById(Long id);

    NotificationRequest createNotification(Long ownerId, String content, UserEntity user);

    ListNotificationDto getNotificationSet(int pageNumber, int pageSize);

    void sendNotification(Long ownerId, Long userId, String message);

    void sendNotification(Long userId,Long taskId);

    void sendNotification(Long userId, File file);

    void sendBatchNotifications(Long ownerId, List<Long> userIds, Set<String> messages);

    //TODO: сделать рассылку

    //TODO: сделать уведомление на телефон


}
