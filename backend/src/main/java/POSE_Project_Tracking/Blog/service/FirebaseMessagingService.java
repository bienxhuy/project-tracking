package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.PushNotificationRequest;
import POSE_Project_Tracking.Blog.dto.PushNotificationResponse;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Service xử lý Firebase Cloud Messaging (FCM) Push Notifications
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {

    /**
     * Gửi notification đến một device token cụ thể
     */
    public PushNotificationResponse sendNotificationToToken(PushNotificationRequest request) {
        try {
            Message message = buildMessage(request);
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            
            log.info("Successfully sent notification to token: {}", request.getToken());
            return PushNotificationResponse.builder()
                    .success(true)
                    .messageId(response)
                    .build();
                    
        } catch (ExecutionException e) {
            // Kiểm tra nếu là lỗi UNREGISTERED (token không còn valid)
            if (e.getCause() instanceof FirebaseMessagingException) {
                FirebaseMessagingException fme = (FirebaseMessagingException) e.getCause();
                String errorCode = fme.getMessagingErrorCode() != null ? 
                        fme.getMessagingErrorCode().name() : "UNKNOWN";
                
                log.error("FCM Error [{}] for token: {} - {}", 
                        errorCode, request.getToken(), fme.getMessage());
                
                return PushNotificationResponse.builder()
                        .success(false)
                        .error("FCM Error: " + errorCode)
                        .fcmErrorCode(errorCode)
                        .tokenInvalid(isTokenInvalidError(errorCode))
                        .build();
            }
            
            log.error("Error sending notification to token: {}", request.getToken(), e);
            return PushNotificationResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
                    
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending notification to token: {}", request.getToken(), e);
            return PushNotificationResponse.builder()
                    .success(false)
                    .error("Request interrupted: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Kiểm tra xem error code có phải là token không valid không
     */
    private boolean isTokenInvalidError(String errorCode) {
        return "UNREGISTERED".equals(errorCode) || 
               "INVALID_ARGUMENT".equals(errorCode) ||
               "NOT_FOUND".equals(errorCode);
    }

    /**
     * Gửi notification đến một topic (nhóm devices)
     */
    public PushNotificationResponse sendNotificationToTopic(PushNotificationRequest request) {
        try {
            Message message = buildMessageForTopic(request);
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            
            log.info("Successfully sent notification to topic: {}", request.getTopic());
            return PushNotificationResponse.builder()
                    .success(true)
                    .messageId(response)
                    .build();
                    
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error sending notification to topic: {}", request.getTopic(), e);
            return PushNotificationResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * Gửi notification cho nhiều devices cùng lúc (multicast)
     */
    public BatchResponse sendMulticastNotification(PushNotificationRequest request, 
                                                   java.util.List<String> tokens) {
        try {
            MulticastMessage message = buildMulticastMessage(request, tokens);
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticastAsync(message).get();
            
            log.info("Successfully sent multicast notification. Success: {}, Failure: {}", 
                    response.getSuccessCount(), response.getFailureCount());
            
            return response;
            
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error sending multicast notification", e);
            throw new RuntimeException("Failed to send multicast notification", e);
        }
    }

    /**
     * Subscribe một device token vào một topic
     */
    public void subscribeToTopic(String token, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopicAsync(java.util.Collections.singletonList(token), topic)
                    .get();
            
            log.info("Successfully subscribed token to topic: {}. Success: {}", 
                    topic, response.getSuccessCount());
                    
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error subscribing to topic: {}", topic, e);
            throw new RuntimeException("Failed to subscribe to topic", e);
        }
    }

    /**
     * Unsubscribe một device token khỏi một topic
     */
    public void unsubscribeFromTopic(String token, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopicAsync(java.util.Collections.singletonList(token), topic)
                    .get();
            
            log.info("Successfully unsubscribed token from topic: {}. Success: {}", 
                    topic, response.getSuccessCount());
                    
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error unsubscribing from topic: {}", topic, e);
            throw new RuntimeException("Failed to unsubscribe from topic", e);
        }
    }

    /**
     * Build Firebase Message cho single device token
     */
    private Message buildMessage(PushNotificationRequest request) {
        Message.Builder messageBuilder = Message.builder()
                .setToken(request.getToken())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .setImage(request.getImageUrl())
                        .build());

        // Thêm custom data nếu có
        if (request.getData() != null && !request.getData().isEmpty()) {
            messageBuilder.putAllData(request.getData());
        }


        return messageBuilder.build();
    }

    /**
     * Build Firebase Message cho topic
     */
    private Message buildMessageForTopic(PushNotificationRequest request) {
        Message.Builder messageBuilder = Message.builder()
                .setTopic(request.getTopic())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .setImage(request.getImageUrl())
                        .build());

        if (request.getData() != null && !request.getData().isEmpty()) {
            messageBuilder.putAllData(request.getData());
        }

        return messageBuilder.build();
    }

    /**
     * Build Firebase Multicast Message
     */
    private MulticastMessage buildMulticastMessage(PushNotificationRequest request, 
                                                   java.util.List<String> tokens) {
        return MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .setImage(request.getImageUrl())
                        .build())
                .putAllData(request.getData() != null ? request.getData() : Map.of())
                .build();
    }
}
