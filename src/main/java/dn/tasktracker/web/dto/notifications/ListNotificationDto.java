package dn.tasktracker.web.dto.notifications;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListNotificationDto {

    private List<NotificationRequest> notifications = new ArrayList<>();

}
