package POSE_Project_Tracking.Blog.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.google.firebase.FirebaseApp;

/**
 * Test Configuration
 * Override beans that are not needed or problematic in test environment
 */
@TestConfiguration
public class TestConfig {

    /**
     * Mock Firebase App for testing
     * This prevents the real FirebaseConfig from trying to initialize
     * with invalid credentials during tests
     */
    @Bean
    @Primary
    public FirebaseApp mockFirebaseApp() {
        // Return null or a mock - tests don't need Firebase
        return null;
    }

    /**
     * Simple in-memory cache manager for tests instead of Redis
     * This provides a working cache without requiring Redis
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}

