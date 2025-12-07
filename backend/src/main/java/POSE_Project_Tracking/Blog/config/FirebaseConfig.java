package POSE_Project_Tracking.Blog.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.extern.slf4j.Slf4j;

/**
 * Firebase Configuration
 * Khởi tạo Firebase Admin SDK để gửi push notifications
 * Reads Firebase credentials from FIREBASE_SERVICE_ACCOUNT_JSON environment variable (JSON format)
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-json:}")
    private String serviceAccountJson;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // Check if Firebase credentials are provided via environment variable
                if (serviceAccountJson == null || serviceAccountJson.trim().isEmpty()) {
                    log.warn("Firebase credentials not provided via FIREBASE_SERVICE_ACCOUNT_JSON environment variable. Skipping Firebase initialization.");
                    return null;
                }

                log.info("Initializing Firebase from environment variable (FIREBASE_SERVICE_ACCOUNT_JSON)");
                
                // Parse JSON from environment variable
                ByteArrayInputStream stream = new ByteArrayInputStream(
                    serviceAccountJson.getBytes(StandardCharsets.UTF_8)
                );
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(stream))
                        .build();

                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("✅ Firebase Admin SDK initialized successfully");
                return app;
            } catch (IOException e) {
                log.error("❌ Failed to initialize Firebase Admin SDK", e);
                throw e;
            }
        }
        log.info("Firebase App already initialized");
        return FirebaseApp.getInstance();
    }
}
