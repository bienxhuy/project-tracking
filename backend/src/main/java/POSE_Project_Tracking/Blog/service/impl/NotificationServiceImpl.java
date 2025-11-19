package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.res.NotificationRes;
import POSE_Project_Tracking.Blog.entity.Notification;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.NotificationMapper;
import POSE_Project_Tracking.Blog.repository.NotificationRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.INotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class NotificationServiceImpl implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public NotificationRes createNotification(Long userId, String message, ENotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);

        return notificationMapper.toResponse(notification);
    }

    @Override
    public NotificationRes getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));

        return notificationMapper.toResponse(notification);
    }

    @Override
    public List<NotificationRes> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationRes> getNotificationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return notificationRepository.findByUser(user).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationRes> getUnreadNotificationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return notificationRepository.findByUserAndIsRead(user, false).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationRes> getNotificationsByType(ENotificationType type) {
        return notificationRepository.findByType(type).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long countUnreadNotificationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        List<Notification> notifications = notificationRepository.findByUserAndIsRead(user, false);
        notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));

        notificationRepository.delete(notification);
    }

    @Override
    public List<NotificationRes> getUnreadNotifications(Long userId) {
        // Alias for getUnreadNotificationsByUser
        return getUnreadNotificationsByUser(userId);
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        // Alias for countUnreadNotificationsByUser
        return countUnreadNotificationsByUser(userId);
    }
}
