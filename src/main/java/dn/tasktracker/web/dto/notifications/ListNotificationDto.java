package dn.tasktracker.web.dto.notifications;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ListNotificationDto implements Serializable {

    private List<NotificationRequest> notifications = new ArrayList<>();

}
