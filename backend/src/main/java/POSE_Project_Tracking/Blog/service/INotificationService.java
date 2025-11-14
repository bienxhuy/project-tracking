package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.res.NotificationRes;
import POSE_Project_Tracking.Blog.enums.ENotificationType;

import java.util.List;

public interface INotificationService {
    NotificationRes createNotification(Long userId, String message, ENotificationType type);
    NotificationRes getNotificationById(Long id);
    List<NotificationRes> getAllNotifications();
    List<NotificationRes> getNotificationsByUser(Long userId);
    List<NotificationRes> getUnreadNotificationsByUser(Long userId);
    List<NotificationRes> getUnreadNotifications(Long userId);  // Alias
    List<NotificationRes> getNotificationsByType(ENotificationType type);
    Long countUnreadNotificationsByUser(Long userId);
    Long countUnreadNotifications(Long userId);  // Alias
    void markAsRead(Long id);
    void markAllAsRead(Long userId);
    void deleteNotification(Long id);
}
