# 📚 Redis Configuration Code Explanation

> Giải thích chi tiết từng đoạn code trong `RedisConfig.java` và `CacheConfig.java`

---

## 📋 Mục Lục
1. [RedisConfig.java](#redisconfig)
2. [CacheConfig.java](#cacheconfig)
3. [Tổng Kết](#tong-ket)

---

## 🔧 RedisConfig.java

### 1. Package và Imports

```java
package POSE_Project_Tracking.Blog.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
```

**Giải thích:**
- `Jackson`: Thư viện để convert Java Object ↔ JSON
- `JavaTimeModule`: Hỗ trợ serialize `LocalDateTime`, `LocalDate`, etc.
- `RedisConnectionFactory`: Factory để tạo connection tới Redis server
- `RedisTemplate`: Class chính để thao tác với Redis
- `Serializer`: Convert dữ liệu trước khi lưu vào Redis

---

### 2. Class Declaration

```java
@Configuration
public class RedisConfig {
```

**Giải thích:**
- `@Configuration`: Đánh dấu đây là class cấu hình Spring
- Spring sẽ quét và load các `@Bean` trong class này khi khởi động

---

### 3. RedisTemplate Bean

```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
```

**Giải thích:**

| Thành phần | Mục đích |
|------------|----------|
| `@Bean` | Tạo bean do Spring quản lý |
| `RedisTemplate<String, Object>` | Generic: Key là String, Value là Object |
| `connectionFactory` | Spring tự động inject (từ `application.properties`) |
| `setConnectionFactory()` | Gán connection đến Redis server |

**Ví dụ thực tế:**
```java
// Key: "user_123" (String)
// Value: User object (Object)
redisTemplate.opsForValue().set("user_123", userObject);
```

---

### 4. Key Serializer

```java
// Use String serializer for keys
StringRedisSerializer stringSerializer = new StringRedisSerializer();
template.setKeySerializer(stringSerializer);
template.setHashKeySerializer(stringSerializer);
```

**Giải thích:**

**Tại sao dùng `StringRedisSerializer`?**
- Redis lưu data dưới dạng **bytes**
- `StringRedisSerializer` convert String → UTF-8 bytes
- Keys trong Redis sẽ human-readable

**Ví dụ:**
```java
// Java code
cache.put("userProfile_123", userData);

// Trong Redis (có thể đọc được)
Key: "userProfile_123"
```

**Nếu không dùng String serializer:**
```
Key: \xAC\xED\x00\x05t\x00\x0FuserProfile_123  ❌ (binary, khó đọc)
```

**Hash Key Serializer:**
- Dùng cho Redis Hash data structure
- `HSET user:123 name "John"` → `name` là hash key

---

### 5. Value Serializer

```java
// Use JSON serializer for values
GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();
template.setValueSerializer(jsonSerializer);
template.setHashValueSerializer(jsonSerializer);
```

**Giải thích:**

**Tại sao dùng JSON?**
- Java Object → JSON String → Redis
- Có thể đọc được trong Redis Commander
- Dễ debug

**Ví dụ:**
```java
// Java Object
User user = new User(123, "John", "john@email.com");

// Lưu vào Redis
redisTemplate.opsForValue().set("user_123", user);

// Trong Redis (JSON format)
{
  "@class": "User",
  "id": 123,
  "name": "John",
  "email": "john@email.com"
}
```

**So sánh các Serializer:**

| Serializer | Ưu điểm | Nhược điểm |
|------------|---------|------------|
| `JdkSerializationRedisSerializer` | Mặc định | Binary, không đọc được |
| `StringRedisSerializer` | Đơn giản | Chỉ dùng cho String |
| `GenericJackson2JsonRedisSerializer` | Human-readable, support complex objects | Tốn space hơn |

---

### 6. After Properties Set

```java
template.afterPropertiesSet();
return template;
```

**Giải thích:**
- `afterPropertiesSet()`: Khởi tạo template sau khi set xong properties
- Validate configuration
- Return bean cho Spring container

---

### 7. Create JSON Serializer (Static Method)

```java
public static GenericJackson2JsonRedisSerializer createJsonSerializer() {
    ObjectMapper objectMapper = new ObjectMapper();
```

**Giải thích:**
- `static`: Có thể gọi mà không cần instance
- `ObjectMapper`: Core class của Jackson để serialize/deserialize
- Dùng chung cho cả `RedisTemplate` và `CacheManager`

---

### 8. Java Time Module

```java
// Support for Java 8 date/time types
objectMapper.registerModule(new JavaTimeModule());
```

**Giải thích:**

**Vấn đề:**
- Mặc định Jackson không biết serialize `LocalDateTime`, `LocalDate`
- Sẽ bị lỗi nếu object có field kiểu này

**Giải pháp:**
- `JavaTimeModule`: Plugin của Jackson
- Biết cách convert `LocalDateTime` → JSON

**Ví dụ:**

```java
// Java Object
@Data
public class Task {
    private Long id;
    private LocalDateTime createdAt;  // Java 8 time
}

// Không có JavaTimeModule
❌ Error: "Cannot serialize LocalDateTime"

// Có JavaTimeModule
✅ JSON: {
    "id": 1,
    "createdAt": "2025-11-15T10:30:00"
}
```

---

### 9. Polymorphic Type Handling

```java
// Enable polymorphic type handling to support inheritance
objectMapper.activateDefaultTyping(
    LaissezFaireSubTypeValidator.instance,
    ObjectMapper.DefaultTyping.NON_FINAL,
    JsonTypeInfo.As.PROPERTY
);
```

**Giải thích:**

**Vấn đề: Type Erasure**
```java
// Lưu vào cache
List<Project> projects = getProjects();
cache.put("projects", projects);  // Type: List

// Lấy ra
List<Project> cached = cache.get("projects");  // Redis chỉ biết là List
// ❌ Không biết element là Project, có thể bị lỗi cast
```

**Giải pháp:**
- `activateDefaultTyping()`: Thêm type info vào JSON
- Redis sẽ lưu kèm class name

**Cấu hình chi tiết:**

| Parameter | Giá trị | Ý nghĩa |
|-----------|---------|---------|
| `LaissezFaireSubTypeValidator.instance` | Validator | Cho phép tất cả subtypes (không strict) |
| `DefaultTyping.NON_FINAL` | Strategy | Áp dụng cho non-final classes |
| `JsonTypeInfo.As.PROPERTY` | Format | Thêm property `@class` vào JSON |

**Ví dụ:**

```java
// Java Object
Project project = new Project(1, "My Project");

// JSON trong Redis (có @class)
{
  "@class": "POSE_Project_Tracking.Blog.entity.Project",
  "id": 1,
  "name": "My Project"
}

// Khi deserialize
Project cached = (Project) cache.get("project_1");
// ✅ Redis biết chính xác phải convert thành Project class
```

**Tại sao cần?**
- Hỗ trợ inheritance (class cha/con)
- Tránh `ClassCastException`
- Deserialize đúng type

---

## ⚙️ CacheConfig.java

### 1. Class Declaration và Annotations

```java
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {
```

**Giải thích:**

| Annotation | Mục đích |
|------------|----------|
| `@Configuration` | Spring configuration class |
| `@EnableCaching` | **Bật Spring Cache Abstraction** |
| `implements CachingConfigurer` | Customize cache behavior |

**`@EnableCaching` làm gì?**
- Scan và xử lý `@Cacheable`, `@CacheEvict`, `@CachePut`
- Tạo proxy cho các method có cache annotations
- Kích hoạt caching infrastructure

**Không có `@EnableCaching`:**
```java
@Cacheable("userProfile")
public User getUserById(Long id) { ... }
// ❌ Annotation bị ignore, không cache gì cả
```

---

### 2. Cache Names Constants

```java
public static final String DASHBOARD_STATS_CACHE = "dashboardStats";
public static final String USER_PROFILE_CACHE = "userProfile";
public static final String PROJECT_LIST_CACHE = "projectList";
public static final String PROJECT_DETAIL_CACHE = "projectDetail";
public static final String MILESTONE_LIST_CACHE = "milestoneList";
public static final String TASK_LIST_CACHE = "taskList";
```

**Giải thích:**

**Tại sao dùng constants?**
- ✅ Type-safe (compile-time checking)
- ✅ Tránh typo
- ✅ Dễ refactor

**Sử dụng:**
```java
// ❌ String literal (dễ sai chính tả)
@Cacheable("userProfiles")  // Typo: userProfiles vs userProfile

// ✅ Dùng constant
@Cacheable(CacheConfig.USER_PROFILE_CACHE)
```

**Cache names trong Redis:**
```
dashboardStats::global
userProfile::123
projectList::all
projectDetail::456
```

---

### 3. TTL Configuration với @Value

```java
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
```

**Giải thích:**

**Syntax: `${property:defaultValue}`**
- `${cache.ttl.dashboard}`: Đọc từ `application.properties`
- `:300`: Default value nếu không tìm thấy property

**Ví dụ:**

**File `application.properties`:**
```properties
cache.ttl.dashboard=300
cache.ttl.user-profile=600
```

**Khi Spring khởi động:**
```java
dashboardTtl = 300      // Từ properties
userProfileTtl = 600    // Từ properties
projectListTtl = 180    // Default (không có trong properties)
```

**Lợi ích:**
- ✅ Dễ thay đổi TTL mà không cần compile lại code
- ✅ Khác nhau giữa dev/staging/prod
- ✅ Fallback value an toàn

---

### 4. Autowired RedisConnectionFactory

```java
@Autowired
private RedisConnectionFactory redisConnectionFactory;
```

**Giải thích:**
- Spring tự động inject `RedisConnectionFactory` bean
- Bean này được tạo tự động từ config trong `application.properties`:
  ```properties
  spring.data.redis.host=localhost
  spring.data.redis.port=6379
  ```

---

### 5. Cache Manager Bean

```java
@Bean
@Override
public CacheManager cacheManager() {
```

**Giải thích:**
- `CacheManager`: Core component của Spring Cache
- Quản lý tất cả cache regions
- Điều phối các thao tác cache (get/put/evict)

**Flow hoạt động:**
```
@Cacheable → Spring Proxy → CacheManager → Redis
```

---

### 6. Default Cache Configuration

```java
RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
    .entryTtl(Duration.ofSeconds(defaultTtl))
    .serializeKeysWith(
        RedisSerializationContext.SerializationPair.fromSerializer(
            new StringRedisSerializer()
        )
    )
    .serializeValuesWith(
        RedisSerializationContext.SerializationPair.fromSerializer(
            RedisConfig.createJsonSerializer()
        )
    )
    .disableCachingNullValues();
```

**Giải thích từng dòng:**

#### a) `defaultCacheConfig()`
- Tạo config mặc định của Redis cache
- Base configuration cho tất cả cache regions

#### b) `entryTtl(Duration.ofSeconds(defaultTtl))`
- **TTL (Time To Live)**: Thời gian tồn tại của cache entry
- `Duration.ofSeconds(300)` = 5 phút
- Sau 5 phút, Redis tự động xóa key

**Ví dụ:**
```java
// T=0: Lưu cache
cache.put("user_123", userData);

// T=299s: Cache còn tồn tại
cache.get("user_123");  // ✅ Hit

// T=301s: Cache đã expire
cache.get("user_123");  // ❌ Miss → Query DB
```

#### c) `serializeKeysWith()`
- Cấu hình cách serialize **keys**
- `StringRedisSerializer`: Key là String UTF-8

**Redis keys:**
```
dashboardStats::global
userProfile::123
projectList::all
```

#### d) `serializeValuesWith()`
- Cấu hình cách serialize **values**
- `RedisConfig.createJsonSerializer()`: Value là JSON

**Redis value:**
```json
{
  "@class": "DashboardStatsDTO",
  "totalProjects": 10,
  "activeProjects": 7
}
```

#### e) `disableCachingNullValues()`
- **Không cache** kết quả `null`
- Tránh cache pollution

**Ví dụ:**
```java
@Cacheable("userProfile")
public User getUserById(Long id) {
    User user = userRepo.findById(id);
    return user;  // null nếu không tìm thấy
}

// Nếu enable caching null:
cache.get("user_999");  // null (cached)
// Mỗi lần call đều trả null từ cache
// ❌ DB có thêm user 999 cũng không query

// Nếu disable caching null:
cache.get("user_999");  // null (not cached)
// Lần sau sẽ query DB lại
// ✅ Có thể lấy được user mới
```

---

### 7. Custom Cache Configurations

```java
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
```

**Giải thích:**

**Tại sao cần custom config?**
- Mỗi loại data có tần suất thay đổi khác nhau
- TTL khác nhau cho từng cache region

**Map structure:**
```java
{
  "dashboardStats" → Config với TTL=300s (5 min),
  "userProfile" → Config với TTL=600s (10 min),
  "projectList" → Config với TTL=180s (3 min),
  "projectDetail" → Config với TTL=300s (5 min)
}
```

**Chiến lược TTL:**

| Cache Region | TTL | Lý do |
|--------------|-----|-------|
| Dashboard Stats | 5 min | Thống kê thay đổi ít, query nặng |
| User Profile | 10 min | Thông tin user ít thay đổi |
| Project List | 3 min | List hay thay đổi (thêm/xóa project) |
| Project Detail | 5 min | Chi tiết project thay đổi vừa phải |

---

### 8. Build RedisCacheManager

```java
return RedisCacheManager.builder(redisConnectionFactory)
    .cacheDefaults(defaultConfig)
    .withInitialCacheConfigurations(cacheConfigurations)
    .build();
```

**Giải thích:**

| Method | Mục đích |
|--------|----------|
| `builder(redisConnectionFactory)` | Tạo builder với Redis connection |
| `cacheDefaults(defaultConfig)` | Config mặc định cho cache regions không có trong map |
| `withInitialCacheConfigurations(...)` | Áp dụng custom config cho các cache regions |
| `build()` | Tạo `CacheManager` instance |

**Flow:**
```
1. Có cache name trong map? 
   YES → Dùng custom config (với TTL riêng)
   NO → Dùng default config (TTL=300s)

2. Tạo cache region trong Redis
3. Ready to use!
```

---

### 9. Custom Key Generator

```java
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
```

**Giải thích:**

**Mục đích:**
- Tạo cache key tự động từ class name, method name, và parameters
- Đảm bảo mỗi method call với params khác nhau có key riêng

**Parameters:**

| Parameter | Kiểu | Ý nghĩa |
|-----------|------|---------|
| `target` | Object | Instance của class chứa method |
| `method` | Method | Method đang được cache |
| `params` | Object[] | Mảng các tham số truyền vào |

**Ví dụ cụ thể:**

```java
// Service class
public class ProjectServiceImpl {
    
    @Cacheable(value = "projectDetail", keyGenerator = "keyGenerator")
    public Project getProjectById(Long id) {
        return projectRepo.findById(id);
    }
}

// Call 1
getProjectById(123L);
// Key generated: "ProjectServiceImpl_getProjectById_123"

// Call 2
getProjectById(456L);
// Key generated: "ProjectServiceImpl_getProjectById_456"

// Call 3
getProjectById(123L);
// Key generated: "ProjectServiceImpl_getProjectById_123"
// ✅ Cache hit! (same key as Call 1)
```

**Key format:**
```
ClassName_methodName_param1_param2_...

Ví dụ:
- ProjectServiceImpl_getProjectById_123
- UserServiceImpl_getUserByUsername_john
- DashboardServiceImpl_getDashboardStats
```

**Null handling:**
```java
for (Object param : params) {
    if (param != null) {  // ✅ Skip null params
        sb.append("_");
        sb.append(param.toString());
    }
}

// getProjectById(null)
// Key: "ProjectServiceImpl_getProjectById"
// (không append null)
```

---

### 10. Custom Error Handler

```java
@Bean
@Override
public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
        // Implementation...
    };
}
```

**Giải thích:**

**Mục đích:**
- Handle errors khi Redis fail
- **Graceful degradation**: App vẫn chạy dù Redis down

#### a) Handle Cache GET Error

```java
@Override
public void handleCacheGetError(RuntimeException exception, 
                                 org.springframework.cache.Cache cache, 
                                 Object key) {
    System.err.println("Cache GET error: " + exception.getMessage());
}
```

**Khi nào xảy ra?**
- Redis server down
- Network timeout
- Deserialization error

**Behavior:**
```java
@Cacheable("userProfile")
public User getUserById(Long id) {
    return userRepo.findById(id);
}

// Redis down
try {
    cache.get("user_123");  // ❌ Error
} catch (Exception e) {
    errorHandler.handleCacheGetError(e, cache, key);
    // Log error nhưng không throw exception
}
// ✅ Vẫn query DB và trả về kết quả
```

#### b) Handle Cache PUT Error

```java
@Override
public void handleCachePutError(RuntimeException exception, 
                                 org.springframework.cache.Cache cache, 
                                 Object key, Object value) {
    System.err.println("Cache PUT error: " + exception.getMessage());
}
```

**Khi nào xảy ra?**
- Redis server down khi lưu cache
- Redis memory full
- Serialization error

**Behavior:**
```java
// Query DB thành công
User user = userRepo.findById(123);

// Lưu vào cache
try {
    cache.put("user_123", user);  // ❌ Redis down
} catch (Exception e) {
    errorHandler.handleCachePutError(e, cache, key, user);
    // Log error
}
// ✅ Vẫn trả về user cho client (dù không cache được)
```

#### c) Handle Cache EVICT Error

```java
@Override
public void handleCacheEvictError(RuntimeException exception, 
                                   org.springframework.cache.Cache cache, 
                                   Object key) {
    System.err.println("Cache EVICT error: " + exception.getMessage());
}
```

**Khi nào xảy ra?**
- Redis down khi xóa cache
- Key không tồn tại

**Behavior:**
```java
@CacheEvict("userProfile")
public void updateUser(User user) {
    userRepo.save(user);
}

// Update DB thành công
userRepo.save(user);  // ✅

// Xóa cache
try {
    cache.evict("user_123");  // ❌ Redis down
} catch (Exception e) {
    errorHandler.handleCacheEvictError(e, cache, key);
    // Log error
}
// ✅ DB đã update, chỉ cache không evict được
```

#### d) Handle Cache CLEAR Error

```java
@Override
public void handleCacheClearError(RuntimeException exception, 
                                   org.springframework.cache.Cache cache) {
    System.err.println("Cache CLEAR error: " + exception.getMessage());
}
```

**Khi nào xảy ra?**
- Redis down khi clear toàn bộ cache region

**Behavior:**
```java
@CacheEvict(value = "projectList", allEntries = true)
public void createProject(Project project) {
    projectRepo.save(project);
}

// Clear toàn bộ projectList cache
try {
    cache.clear();  // ❌ Redis down
} catch (Exception e) {
    errorHandler.handleCacheClearError(e, cache);
    // Log error
}
// ✅ Method vẫn hoàn thành
```

---

### 11. Cache Resolver

```java
@Override
public CacheResolver cacheResolver() {
    return null; // Use default
}
```

**Giải thích:**
- `CacheResolver`: Xác định cache nào sẽ được dùng
- `null`: Dùng default resolver
- Default behavior: Dùng cache name trong `@Cacheable` annotation

**Custom resolver (nâng cao):**
```java
// Có thể implement để dynamic chọn cache based on runtime conditions
// Ví dụ: User role khác nhau dùng cache khác nhau
```

---

## 🎯 Tổng Kết

### Flow Hoàn Chỉnh

```
1. Application Start
   ↓
2. Spring Boot Auto-Configuration
   - Đọc application.properties (Redis host, port)
   - Tạo RedisConnectionFactory bean
   ↓
3. Load RedisConfig
   - Tạo RedisTemplate bean
   - Configure serializers (String keys, JSON values)
   ↓
4. Load CacheConfig
   - @EnableCaching kích hoạt cache abstraction
   - Tạo CacheManager bean
   - Định nghĩa 6 cache regions với TTL riêng
   - Tạo KeyGenerator bean
   - Tạo ErrorHandler bean
   ↓
5. Application Ready
   - @Cacheable, @CacheEvict, @CachePut hoạt động
   - Data được cache vào Redis
```

---

### Cache Operation Flow

```java
// 1. Client request
GET /api/projects/123

// 2. Spring Proxy intercept
@Cacheable("projectDetail")
public Project getProjectById(Long id) { ... }

// 3. Generate cache key
KeyGenerator → "ProjectServiceImpl_getProjectById_123"

// 4. Check cache
CacheManager → Redis GET "projectDetail::ProjectServiceImpl_getProjectById_123"

// 5a. Cache HIT
return cached_value;  // ⚡ Fast (1-2ms)

// 5b. Cache MISS
→ Execute method (query DB)  // 🐌 Slow (50-200ms)
→ Store result in Redis
→ Return result
```

---

### Interaction Between Components

```
┌─────────────────┐
│ @Cacheable      │ → Annotation trên service method
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│ Spring Proxy    │ → Intercept method call
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│ CacheManager    │ → Quản lý cache operations
│ (CacheConfig)   │ → Chọn cache region & TTL
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│ KeyGenerator    │ → Tạo cache key từ params
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│ RedisTemplate   │ → Thực hiện GET/SET với Redis
│ (RedisConfig)   │ → Serialize/Deserialize data
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│ Redis Server    │ → Lưu trữ data trong RAM
└─────────────────┘
```

---

### Key Concepts Summary

| Concept | File | Mục đích |
|---------|------|----------|
| **RedisTemplate** | RedisConfig | Low-level Redis operations |
| **CacheManager** | CacheConfig | High-level cache abstraction |
| **Serializer** | RedisConfig | Convert Java ↔ bytes |
| **KeyGenerator** | CacheConfig | Tạo cache key |
| **ErrorHandler** | CacheConfig | Handle Redis failures |
| **TTL** | CacheConfig | Auto-expire cache entries |
| **Cache Regions** | CacheConfig | Nhóm cache với config riêng |

---

### Best Practices Applied

1. ✅ **Separation of Concerns**
   - `RedisConfig`: Connection & serialization
   - `CacheConfig`: Cache management & strategy

2. ✅ **Type Safety**
   - Constants cho cache names
   - Tránh string literals

3. ✅ **Flexible Configuration**
   - `@Value` cho TTL từ properties
   - Dễ thay đổi per environment

4. ✅ **Error Resilience**
   - Custom ErrorHandler
   - App vẫn chạy khi Redis down

5. ✅ **Human-Readable**
   - String keys
   - JSON values
   - Dễ debug trong Redis Commander

6. ✅ **Performance**
   - Appropriate TTL cho từng data type
   - Avoid caching null values

---

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| `ClassCastException` | ✅ Enable polymorphic typing |
| `LocalDateTime` serialize error | ✅ Register JavaTimeModule |
| Duplicate bean 'cacheManager' | ✅ Remove from RedisConfig |
| Redis down breaks app | ✅ Use ErrorHandler |
| Cache key collision | ✅ Use custom KeyGenerator |
| Stale data | ✅ Set appropriate TTL & use @CacheEvict |

---

## 📚 Further Reading

- [Spring Cache Abstraction](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [Redis Documentation](https://redis.io/docs/)
- [Jackson ObjectMapper](https://github.com/FasterXML/jackson-docs)

---

**Created**: November 15, 2025  
**Author**: Project Tracking Team  
**Status**: ✅ Complete

