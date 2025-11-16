package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.PushNotificationRequest;
import POSE_Project_Tracking.Blog.dto.PushNotificationResponse;
import POSE_Project_Tracking.Blog.service.FirebaseMessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * REST Controller for Firebase Push Notifications
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Push Notifications", description = "Firebase Cloud Messaging APIs")
public class PushNotificationController {

    private final FirebaseMessagingService messagingService;

    /**
     * Gửi notification đến một device token cụ thể
     */
    @PostMapping("/send")
    @Operation(summary = "Send notification to a specific device token")
    public ResponseEntity<PushNotificationResponse> sendNotification(
            @RequestBody PushNotificationRequest request) {
        
        log.info("Sending notification to token: {}", request.getToken());
        PushNotificationResponse response = messagingService.sendNotificationToToken(request);
        
        return response.isSuccess() 
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * Gửi notification đến một topic
     */
    @PostMapping("/send-to-topic")
    @Operation(summary = "Send notification to all devices subscribed to a topic")
    public ResponseEntity<PushNotificationResponse> sendNotificationToTopic(
            @RequestBody PushNotificationRequest request) {
        
        log.info("Sending notification to topic: {}", request.getTopic());
        PushNotificationResponse response = messagingService.sendNotificationToTopic(request);
        
        return response.isSuccess() 
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * Subscribe một device token vào topic
     */
    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe a device token to a topic")
    public ResponseEntity<String> subscribeToTopic(
            @RequestParam String token,
            @RequestParam String topic) {
        
        log.info("Subscribing token to topic: {}", topic);
        messagingService.subscribeToTopic(token, topic);
        
        return ResponseEntity.ok("Successfully subscribed to topic: " + topic);
    }

    /**
     * Unsubscribe một device token khỏi topic
     */
    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe a device token from a topic")
    public ResponseEntity<String> unsubscribeFromTopic(
            @RequestParam String token,
            @RequestParam String topic) {
        
        log.info("Unsubscribing token from topic: {}", topic);
        messagingService.unsubscribeFromTopic(token, topic);
        
        return ResponseEntity.ok("Successfully unsubscribed from topic: " + topic);
    }

    /**
     * Test endpoint để kiểm tra Firebase đã được cấu hình chưa
     */
    @GetMapping("/test")
    @Operation(summary = "Test if Firebase is configured correctly")
    public ResponseEntity<String> testFirebaseConfig() {
        return ResponseEntity.ok("Firebase Push Notification is configured and ready!");
    }
}
