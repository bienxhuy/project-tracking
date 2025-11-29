package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.PushNotificationRequest;
import POSE_Project_Tracking.Blog.dto.PushNotificationResponse;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.UserDeviceToken;
import POSE_Project_Tracking.Blog.service.FirebaseMessagingService;
import POSE_Project_Tracking.Blog.service.UserDeviceTokenService;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final SecurityUtil securityUtil;

    /**
     * Đăng ký device token cho user hiện tại
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new device token for the current user")
    public ResponseEntity<UserDeviceToken> registerDeviceToken(
            @RequestBody Map<String, String> request) {
        
        User currentUser = securityUtil.getCurrentUser();
        
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
    public ResponseEntity<List<UserDeviceToken>> getMyDeviceTokens() {
        User currentUser = securityUtil.getCurrentUser();
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
    public ResponseEntity<String> deleteAllMyDeviceTokens() {
        User currentUser = securityUtil.getCurrentUser();
        deviceTokenService.deleteAllUserDeviceTokens(currentUser);
        return ResponseEntity.ok("All device tokens deleted successfully");
    }

    /**
     * Debug endpoint - xem chi tiết các tokens
     */
    @GetMapping("/debug")
    @Operation(summary = "Debug: View detailed information about stored tokens")
    public ResponseEntity<Map<String, Object>> debugTokens() {
        User currentUser = securityUtil.getCurrentUser();
        List<UserDeviceToken> tokens = deviceTokenService.getActiveDeviceTokens(currentUser);
        
        return ResponseEntity.ok(Map.of(
            "userId", currentUser.getId(),
            "username", currentUser.getUsername(),
            "totalTokens", tokens.size(),
            "tokens", tokens.stream().map(token -> Map.of(
                "id", token.getId(),
                "fcmToken", token.getFcmToken(),
                "deviceType", token.getDeviceType(),
                "deviceInfo", token.getDeviceInfo() != null ? token.getDeviceInfo() : "N/A",
                "isActive", token.getIsActive(),
                "createdAt", token.getCreatedAt(),
                "lastUsedAt", token.getLastUsedAt()
            )).toList()
        ));
    }

    /**
     * Gửi test notification đến user hiện tại
     */
    @PostMapping("/test-notification")
    @Operation(summary = "Send a test notification to all devices of the current user")
    public ResponseEntity<Map<String, Object>> sendTestNotification() {
        
        User currentUser = securityUtil.getCurrentUser();
        List<String> tokens = deviceTokenService.getActiveFcmTokens(currentUser);
        
        if (tokens.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "No active device tokens found",
                "results", List.of()
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
                    PushNotificationResponse response = messagingService.sendNotificationToToken(notification);
                    
                    // Tự động xóa token không valid khỏi database
                    if (response.isTokenInvalid()) {
                        log.warn("Removing invalid token from database: {}", token);
                        deviceTokenService.deleteDeviceToken(token);
                    }
                    
                    return response;
                })
                .toList();
        
        long successCount = responses.stream().filter(PushNotificationResponse::isSuccess).count();
        long failureCount = responses.size() - successCount;
        long invalidTokenCount = responses.stream().filter(PushNotificationResponse::isTokenInvalid).count();
        
        return ResponseEntity.ok(Map.of(
            "success", successCount > 0,
            "message", String.format("Sent to %d/%d devices. %d invalid tokens removed.", 
                    successCount, responses.size(), invalidTokenCount),
            "successCount", successCount,
            "failureCount", failureCount,
            "invalidTokensRemoved", invalidTokenCount,
            "results", responses
        ));
    }
}
