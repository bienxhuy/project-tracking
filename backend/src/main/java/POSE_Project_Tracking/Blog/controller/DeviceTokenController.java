package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.PushNotificationRequest;
import POSE_Project_Tracking.Blog.dto.PushNotificationResponse;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.UserDeviceToken;
import POSE_Project_Tracking.Blog.service.FirebaseMessagingService;
import POSE_Project_Tracking.Blog.service.UserDeviceTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing user device tokens
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/device-tokens")
@RequiredArgsConstructor
@Tag(name = "Device Tokens", description = "APIs for managing user FCM device tokens")
public class DeviceTokenController {

    private final UserDeviceTokenService deviceTokenService;
    private final FirebaseMessagingService messagingService;

    /**
     * Đăng ký device token cho user hiện tại
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new device token for the current user")
    public ResponseEntity<UserDeviceToken> registerDeviceToken(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        String fcmToken = request.get("fcmToken");
        String deviceType = request.getOrDefault("deviceType", "UNKNOWN");
        String deviceInfo = request.getOrDefault("deviceInfo", "");
        
        UserDeviceToken token = deviceTokenService.saveDeviceToken(
                currentUser, fcmToken, deviceType, deviceInfo);
        
        log.info("Registered device token for user: {}", currentUser.getId());
        return ResponseEntity.ok(token);
    }

    /**
     * Lấy tất cả device tokens của user hiện tại
     */
    @GetMapping("/my-tokens")
    @Operation(summary = "Get all device tokens of the current user")
    public ResponseEntity<List<UserDeviceToken>> getMyDeviceTokens(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<UserDeviceToken> tokens = deviceTokenService.getActiveDeviceTokens(currentUser);
        return ResponseEntity.ok(tokens);
    }

    /**
     * Xóa một device token
     */
    @DeleteMapping("/{fcmToken}")
    @Operation(summary = "Delete a device token")
    public ResponseEntity<String> deleteDeviceToken(@PathVariable String fcmToken) {
        deviceTokenService.deleteDeviceToken(fcmToken);
        return ResponseEntity.ok("Device token deleted successfully");
    }

    /**
     * Vô hiệu hóa một device token
     */
    @PostMapping("/deactivate/{fcmToken}")
    @Operation(summary = "Deactivate a device token")
    public ResponseEntity<String> deactivateDeviceToken(@PathVariable String fcmToken) {
        deviceTokenService.deactivateDeviceToken(fcmToken);
        return ResponseEntity.ok("Device token deactivated successfully");
    }

    /**
     * Xóa tất cả device tokens của user hiện tại (logout all devices)
     */
    @DeleteMapping("/my-tokens/all")
    @Operation(summary = "Delete all device tokens of the current user")
    public ResponseEntity<String> deleteAllMyDeviceTokens(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        deviceTokenService.deleteAllUserDeviceTokens(currentUser);
        return ResponseEntity.ok("All device tokens deleted successfully");
    }

    /**
     * Gửi test notification đến user hiện tại
     */
    @PostMapping("/test-notification")
    @Operation(summary = "Send a test notification to all devices of the current user")
    public ResponseEntity<List<PushNotificationResponse>> sendTestNotification(
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        List<String> tokens = deviceTokenService.getActiveFcmTokens(currentUser);
        
        if (tokens.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of(
                PushNotificationResponse.builder()
                    .success(false)
                    .error("No active device tokens found")
                    .build()
            ));
        }
        
        PushNotificationRequest notification = PushNotificationRequest.builder()
                .title("Test Notification")
                .body("This is a test notification from Project Tracking System!")
                .data(Map.of(
                    "type", "TEST",
                    "timestamp", String.valueOf(System.currentTimeMillis())
                ))
                .build();
        
        List<PushNotificationResponse> responses = tokens.stream()
                .map(token -> {
                    notification.setToken(token);
                    return messagingService.sendNotificationToToken(notification);
                })
                .toList();
        
        return ResponseEntity.ok(responses);
    }
}
