package POSE_Project_Tracking.Blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache Configuration
 * Defines cache names, TTL for different cache regions, and custom key generator
 */
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    // Cache names constants
    public static final String DASHBOARD_STATS_CACHE = "dashboardStats";
    public static final String USER_PROFILE_CACHE = "userProfile";
    public static final String PROJECT_LIST_CACHE = "projectList";
    public static final String PROJECT_DETAIL_CACHE = "projectDetail";
    public static final String MILESTONE_LIST_CACHE = "milestoneList";
    public static final String TASK_LIST_CACHE = "taskList";

    @Value("${cache.ttl.dashboard:300}")
    private long dashboardTtl;

    @Value("${cache.ttl.user-profile:600}")
    private long userProfileTtl;

    @Value("${cache.ttl.project-list:180}")
    private long projectListTtl;

    @Value("${cache.ttl.project-detail:300}")
    private long projectDetailTtl;

    @Value("${cache.ttl.default:300}")
    private long defaultTtl;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * Configure cache manager with different TTL for different cache regions
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultTtl))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisConfig.createJsonSerializer())
                )
                .disableCachingNullValues();

        // Custom cache configurations with different TTL
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        cacheConfigurations.put(DASHBOARD_STATS_CACHE, 
                defaultConfig.entryTtl(Duration.ofSeconds(dashboardTtl)));
        
        cacheConfigurations.put(USER_PROFILE_CACHE, 
                defaultConfig.entryTtl(Duration.ofSeconds(userProfileTtl)));
        
        cacheConfigurations.put(PROJECT_LIST_CACHE, 
                defaultConfig.entryTtl(Duration.ofSeconds(projectListTtl)));
        
        cacheConfigurations.put(PROJECT_DETAIL_CACHE, 
                defaultConfig.entryTtl(Duration.ofSeconds(projectDetailTtl)));
        
        cacheConfigurations.put(MILESTONE_LIST_CACHE, 
                defaultConfig.entryTtl(Duration.ofSeconds(projectListTtl)));
        
        cacheConfigurations.put(TASK_LIST_CACHE, 
                defaultConfig.entryTtl(Duration.ofSeconds(projectListTtl)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Custom key generator for cache keys
     * Generates keys based on class name, method name, and parameters
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName());
            sb.append("_");
            sb.append(method.getName());
            for (Object param : params) {
                if (param != null) {
                    sb.append("_");
                    sb.append(param.toString());
                }
            }
            return sb.toString();
        };
    }

    /**
     * Custom error handler for cache operations
     * Logs errors but doesn't break the application flow
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                // Log error but continue execution
                System.err.println("Cache GET error: " + exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                // Log error but continue execution
                System.err.println("Cache PUT error: " + exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                // Log error but continue execution
                System.err.println("Cache EVICT error: " + exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                // Log error but continue execution
                System.err.println("Cache CLEAR error: " + exception.getMessage());
            }
        };
    }

    @Override
    public CacheResolver cacheResolver() {
        return null; // Use default
    }
}
