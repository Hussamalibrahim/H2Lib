package com.library.library.Model.DtoMapper;


import com.library.library.Model.Dto.NotificationDto;
import com.library.library.Model.Notification;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotificationsMapper {

    public static Notification convertToEntity(NotificationDto notificationsDto) {
        Notification notifications = new Notification();

        notifications.setId(notificationsDto.getId());
        notifications.setMassage(notificationsDto.getMassage());
        notifications.setCreatedAt(notificationsDto.getCreatedAt());
        notifications.setTargetId(notificationsDto.getTargetId());
        notifications.setIsRead(notificationsDto.getIsRead());

        return notifications;
    }

    public static NotificationDto convertToDto(Notification notifications) {
        NotificationDto notificationsDto = new NotificationDto();

        notificationsDto.setId(notifications.getId());
        notificationsDto.setMassage(notifications.getMassage());
        notificationsDto.setCreatedAt(notifications.getCreatedAt());
        notificationsDto.setTargetId(notifications.getTargetId());
        notificationsDto.setIsRead(notifications.getIsRead());

        return notificationsDto;
    }
}
