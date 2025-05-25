package dn.tasktracker.web.mapper;

import dn.tasktracker.web.dto.notifications.ListNotificationDto;
import dn.tasktracker.web.dto.notifications.NotificationDto;
import dn.tasktracker.entity.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends Mappable<NotificationEntity, NotificationDto> {

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
    NotificationDto toDto(NotificationEntity entity);
}
