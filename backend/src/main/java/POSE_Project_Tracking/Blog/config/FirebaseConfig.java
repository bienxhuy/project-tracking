package POSE_Project_Tracking.Blog.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-file}")
    private String serviceAccountPath;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Skip Firebase initialization if path is empty or file doesn't exist (e.g., in test environment)
        if (serviceAccountPath == null || serviceAccountPath.trim().isEmpty()) {
            log.warn("Firebase service account file path is empty. Skipping Firebase initialization.");
            return null;
        }

        File serviceAccountFile = new File(serviceAccountPath);
        if (!serviceAccountFile.exists()) {
            log.warn("Firebase service account file not found: {}. Skipping Firebase initialization.", serviceAccountPath);
            return null;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try (FileInputStream serviceAccount = new FileInputStream(serviceAccountPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK initialized successfully");
                return app;
            } catch (IOException e) {
                log.error("Failed to initialize Firebase Admin SDK", e);
                throw e;
            }
        }
        log.info("Firebase App already initialized");
        return FirebaseApp.getInstance();
    }
}
