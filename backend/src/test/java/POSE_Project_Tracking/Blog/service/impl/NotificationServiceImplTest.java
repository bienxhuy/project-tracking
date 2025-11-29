package POSE_Project_Tracking.Blog.service.impl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import POSE_Project_Tracking.Blog.dto.res.NotificationRes;
import POSE_Project_Tracking.Blog.entity.Notification;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.NotificationMapper;
import POSE_Project_Tracking.Blog.repository.NotificationRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl service;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        notification = Notification.builder()
                .id(1L)
                .user(user)
                .message("Test notification")
                .type(ENotificationType.TASK_ASSIGNED)
                .isRead(false)
                .build();
    }

    @Test
    void createNotification_validRequest_createsNotification() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.toResponse(notification)).thenReturn(new NotificationRes());

        NotificationRes result = service.createNotification(1L, "Test message", ENotificationType.TASK_ASSIGNED);

        assertNotNull(result);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_userNotFound_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, 
            () -> service.createNotification(999L, "Test", ENotificationType.TASK_ASSIGNED));
    }

    @Test
    void markAsRead_existingNotification_marksAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        service.markAsRead(1L);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAllAsRead_userHasUnreadNotifications_marksAllAsRead() {
        Notification notif1 = Notification.builder().isRead(false).build();
        Notification notif2 = Notification.builder().isRead(false).build();
        List<Notification> unreadNotifs = List.of(notif1, notif2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserAndIsRead(user, false)).thenReturn(unreadNotifs);
        when(notificationRepository.saveAll(unreadNotifs)).thenReturn(unreadNotifs);

        service.markAllAsRead(1L);

        assertTrue(notif1.getIsRead());
        assertTrue(notif2.getIsRead());
        verify(notificationRepository).saveAll(unreadNotifs);
    }

    @Test
    void countUnreadNotificationsByUser_userExists_returnsCount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.countByUserAndIsRead(user, false)).thenReturn(5L);

        Long count = service.countUnreadNotificationsByUser(1L);

        assertEquals(5L, count);
    }

    @Test
    void deleteNotification_existingNotification_deletesNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        service.deleteNotification(1L);

        verify(notificationRepository).delete(notification);
    }

    @Test
    void getNotificationById_nonExistingNotification_throwsException() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getNotificationById(999L));
    }
}

