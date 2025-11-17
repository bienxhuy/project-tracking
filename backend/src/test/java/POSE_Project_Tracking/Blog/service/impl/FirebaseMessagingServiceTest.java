package POSE_Project_Tracking.Blog.service.impl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.FirebaseMessaging;

import POSE_Project_Tracking.Blog.dto.PushNotificationRequest;
import POSE_Project_Tracking.Blog.dto.PushNotificationResponse;
import POSE_Project_Tracking.Blog.service.FirebaseMessagingService;

@ExtendWith(MockitoExtension.class)
class FirebaseMessagingServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FirebaseMessagingService service;

    private PushNotificationRequest request;

    @BeforeEach
    void setUp() {
        request = PushNotificationRequest.builder()
                .token("test-token")
                .title("Test Title")
                .body("Test Body")
                .data(Map.of("key", "value"))
                .build();
    }

    @Test
    void sendNotificationToToken_validRequest_returnsSuccess() {
        try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            ApiFuture<String> future = ApiFutures.immediateFuture("message-id-123");
            when(firebaseMessaging.sendAsync(any())).thenReturn(future);

            PushNotificationResponse response = service.sendNotificationToToken(request);

            assertTrue(response.isSuccess());
            assertEquals("message-id-123", response.getMessageId());
        }
    }

    @Test
    void sendNotificationToToken_exception_returnsFailure() {
        try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            ApiFuture<String> future = ApiFutures.immediateFailedFuture(new RuntimeException("Test error"));
            when(firebaseMessaging.sendAsync(any())).thenReturn(future);

            PushNotificationResponse response = service.sendNotificationToToken(request);

            assertFalse(response.isSuccess());
            assertNotNull(response.getError());
        }
    }

    @Test
    void sendNotificationToTopic_validRequest_returnsSuccess() {
        request.setTopic("test-topic");
        
        try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            ApiFuture<String> future = ApiFutures.immediateFuture("message-id-456");
            when(firebaseMessaging.sendAsync(any())).thenReturn(future);

            PushNotificationResponse response = service.sendNotificationToTopic(request);

            assertTrue(response.isSuccess());
            assertEquals("message-id-456", response.getMessageId());
        }
    }

    @Test
    void sendNotificationToToken_withData_sendsSuccessfully() {
        request.setData(Map.of("action", "open_task", "taskId", "123"));
        
        try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            ApiFuture<String> future = ApiFutures.immediateFuture("message-id-789");
            when(firebaseMessaging.sendAsync(any())).thenReturn(future);

            PushNotificationResponse response = service.sendNotificationToToken(request);

            assertTrue(response.isSuccess());
            assertNotNull(response.getMessageId());
        }
    }
}

