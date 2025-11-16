package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.NotificationRes;
import POSE_Project_Tracking.Blog.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    // Lấy notification theo ID
    @GetMapping("/{id}")
    public ApiResponse<NotificationRes> getNotificationById(@PathVariable Long id) {
        NotificationRes notification = notificationService.getNotificationById(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin thông báo thành công", notification, null);
    }

    // Lấy notifications theo user
    @GetMapping("/user/{userId}")
    public ApiResponse<List<NotificationRes>> getNotificationsByUser(@PathVariable Long userId) {
        List<NotificationRes> notifications = notificationService.getNotificationsByUser(userId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách thông báo của người dùng thành công", notifications, null);
    }

    // Lấy notifications chưa đọc
    @GetMapping("/user/{userId}/unread")
    public ApiResponse<List<NotificationRes>> getUnreadNotifications(@PathVariable Long userId) {
        List<NotificationRes> notifications = notificationService.getUnreadNotifications(userId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách thông báo chưa đọc thành công", notifications, null);
    }

    // Đếm số notifications chưa đọc
    @GetMapping("/user/{userId}/unread/count")
    public ApiResponse<Long> countUnreadNotifications(@PathVariable Long userId) {
        Long count = notificationService.countUnreadNotifications(userId);
        return new ApiResponse<>(HttpStatus.OK, "Đếm thông báo chưa đọc thành công", count, null);
    }

    // Đánh dấu đã đọc
    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return new ApiResponse<>(HttpStatus.OK, "Đánh dấu thông báo đã đọc thành công", null, null);
    }

    // Đánh dấu tất cả đã đọc
    @PatchMapping("/user/{userId}/read-all")
    public ApiResponse<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return new ApiResponse<>(HttpStatus.OK, "Đánh dấu tất cả thông báo đã đọc thành công", null, null);
    }

    // Xóa notification
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa thông báo thành công", null, null);
    }
}
