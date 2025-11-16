package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.WebSocketNotificationMessage;
import POSE_Project_Tracking.Blog.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Debug endpoints for testing WebSocket functionality
 * Remove or secure in production!
 */
@Slf4j
@RestController
@RequestMapping("/api/debug/websocket")
@RequiredArgsConstructor
public class WebSocketDebugController {

    private final WebSocketNotificationService webSocketNotificationService;
    private final SimpUserRegistry simpUserRegistry;

    /**
     * Get all active WebSocket sessions
     * GET /api/debug/websocket/sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getActiveSessions() {
        Set<SimpUser> users = simpUserRegistry.getUsers();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", users.size());
        
        List<Map<String, Object>> userList = new ArrayList<>();
        for (SimpUser user : users) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getName()); // Principal (userId)
            userInfo.put("sessionCount", user.getSessions().size());
            
            List<String> sessionIds = user.getSessions().stream()
                .map(session -> session.getId())
                .collect(Collectors.toList());
            userInfo.put("sessions", sessionIds);
            
            userList.add(userInfo);
        }
        result.put("users", userList);
        
        log.info("Active WebSocket sessions: {}", result);
        return ResponseEntity.ok(result);
    }

    /**
     * Send test notification to specific user
     * POST /api/debug/websocket/test-notification/{userId}
     */
    @PostMapping("/test-notification/{userId}")
    public ResponseEntity<Map<String, String>> sendTestNotification(@PathVariable Long userId) {
        WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
            .id(999L)
            .title("🧪 Test Notification")
            .message("This is a test notification sent from debug endpoint at " + LocalDateTime.now())
            .type("INFO")
            .action("NEW_NOTIFICATION")
            .timestamp(LocalDateTime.now().toString())
            .build();
        
        log.info("Sending test notification to user {}: {}", userId, message);
        webSocketNotificationService.sendNotificationToUser(userId, message);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Test notification sent to user " + userId);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Send test count update to specific user
     * POST /api/debug/websocket/test-count/{userId}/{count}
     */
    @PostMapping("/test-count/{userId}/{count}")
    public ResponseEntity<Map<String, String>> sendTestCount(
            @PathVariable Long userId,
            @PathVariable Integer count) {
        
        log.info("Sending test count {} to user {}", count, userId);
        webSocketNotificationService.sendNotificationCount(userId, count);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Test count " + count + " sent to user " + userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Broadcast test notification to all users
     * POST /api/debug/websocket/broadcast
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> broadcastTest() {
        WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
            .id(888L)
            .title("📢 Broadcast Test")
            .message("This is a broadcast test notification at " + LocalDateTime.now())
            .type("INFO")
            .action("NEW_NOTIFICATION")
            .timestamp(LocalDateTime.now().toString())
            .build();
        
        log.info("Broadcasting test notification: {}", message);
        webSocketNotificationService.broadcastNotification(message);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Test broadcast sent to all users");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Send multiple test notifications
     * POST /api/debug/websocket/test-multiple/{userId}/{count}
     */
    @PostMapping("/test-multiple/{userId}/{count}")
    public ResponseEntity<Map<String, String>> sendMultipleNotifications(
            @PathVariable Long userId,
            @PathVariable Integer count) {
        
        log.info("Sending {} test notifications to user {}", count, userId);
        
        for (int i = 1; i <= count; i++) {
            WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
                .id((long) (1000 + i))
                .title("Test Notification #" + i)
                .message("This is test notification number " + i + " of " + count)
                .type("INFO")
                .action("NEW_NOTIFICATION")
                .timestamp(LocalDateTime.now().toString())
                .build();
            
            webSocketNotificationService.sendNotificationToUser(userId, message);
            
            // Small delay to see them arrive separately
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", count + " test notifications sent to user " + userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if specific user has active sessions
     * GET /api/debug/websocket/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserSessions(@PathVariable String userId) {
        SimpUser user = simpUserRegistry.getUser(userId);
        
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("userId", userId);
            result.put("connected", true);
            result.put("sessionCount", user.getSessions().size());
            
            List<String> sessionIds = user.getSessions().stream()
                .map(session -> session.getId())
                .collect(Collectors.toList());
            result.put("sessions", sessionIds);
        } else {
            result.put("userId", userId);
            result.put("connected", false);
            result.put("sessionCount", 0);
            result.put("sessions", Collections.emptyList());
        }
        
        log.info("User {} sessions: {}", userId, result);
        return ResponseEntity.ok(result);
    }
}
