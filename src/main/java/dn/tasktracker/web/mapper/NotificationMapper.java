package dn.tasktracker.web.mapper;

import dn.tasktracker.web.dto.notifications.ListNotificationDto;
import dn.tasktracker.entity.NotificationEntity;
import dn.tasktracker.web.dto.notifications.NotificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper extends Mappable<NotificationEntity, NotificationRequest> {

    default ListNotificationDto toDtoWithNotificationList(List<NotificationEntity> notifications){
        ListNotificationDto listNotificationDto = new ListNotificationDto();
        listNotificationDto.setNotifications(
                notifications.stream()
                        .map(this::toDto)
                        .toList());
        return listNotificationDto;
    }

    @Override
    @Mapping(source = "user.id",target = "from")
    @Mapping(source = "createdAt",target = "createdAt",dateFormat = "dd/MM/yyyy HH:mm:ss")
    NotificationRequest toDto(NotificationEntity entity);
}
