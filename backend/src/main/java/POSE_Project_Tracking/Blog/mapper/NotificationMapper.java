package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.res.NotificationRes;
import POSE_Project_Tracking.Blog.entity.Notification;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "triggeredBy", source = "triggeredBy")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "referenceId", source = "referenceId")
    @Mapping(target = "referenceType", source = "referenceType")
    Notification toEntity(User user, User triggeredBy, ENotificationType type, 
                         String title, String message, Long referenceId, String referenceType);

    @Mapping(target = "triggeredById", source = "triggeredBy.id")
    @Mapping(target = "triggeredByName", source = "triggeredBy.displayName")
    NotificationRes toResponse(Notification notification);
}
