# 🚀 Redis Cache - Quick Start Guide

## Bước 1: Cài đặt Redis

### Sử dụng Docker (Khuyến nghị)

```bash
# Di chuyển vào thư mục backend
cd backend

# Khởi động Redis với Docker Compose
docker-compose -f docker-compose.redis.yml up -d

# Kiểm tra trạng thái
docker ps | grep redis
```

Bạn sẽ có:
- **Redis**: http://localhost:6379
- **Redis Commander** (GUI): http://localhost:8081

## Bước 2: Cấu hình Environment

Thêm vào file `.env` của bạn:

```properties
# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```

Hoặc nếu dùng Docker Compose trong cùng network:
```properties
REDIS_HOST=redis
```

## Bước 3: Build và Run Application

```bash
# Clean và build project
mvn clean install

# Run application
mvn spring-boot:run
```

## Bước 4: Test Cache

### 1. Test Dashboard API

```bash
# Lần đầu (slow - không có cache)
curl http://localhost:9090/api/dashboard/stats

# Lần sau (fast - có cache)
curl http://localhost:9090/api/dashboard/stats
```

### 2. Monitor Redis

Mở Redis Commander: http://localhost:8081

Hoặc dùng Redis CLI:
```bash
# Vào Redis CLI
docker exec -it project-tracking-redis redis-cli

# Xem tất cả keys
KEYS *

# Output ví dụ:
# 1) "dashboardStats::global"
# 2) "projectList::all"
# 3) "userProfile::1"

# Xem giá trị cache
GET "dashboardStats::global"

# Xem thời gian sống còn lại (TTL)
TTL "dashboardStats::global"
```

## Bước 5: Test Cache Invalidation

```bash
# 1. Get dashboard stats (cache sẽ được tạo)
curl http://localhost:9090/api/dashboard/stats

# 2. Tạo project mới (cache sẽ bị xóa)
curl -X POST http://localhost:9090/api/projects \
  -H "Content-Type: application/json" \
  -d '{...}'

# 3. Get dashboard stats lại (cache sẽ được tạo mới)
curl http://localhost:9090/api/dashboard/stats
```

## 📊 Cached Endpoints

| Endpoint | Cache Time | Auto Clear On |
|----------|------------|---------------|
| `GET /api/dashboard/stats` | 5 min | Project/Task changes |
| `GET /api/projects` | 3 min | Project CRUD |
| `GET /api/projects/{id}` | 5 min | Project update/delete |
| `GET /api/users/{id}` | 10 min | User update |

## 🔧 Useful Commands

### Stop Redis
```bash
docker-compose -f docker-compose.redis.yml down
```

### Clear All Cache
```bash
# Via API
curl -X POST http://localhost:9090/api/dashboard/cache/refresh

# Via Redis CLI
docker exec -it project-tracking-redis redis-cli FLUSHDB
```

### View Redis Logs
```bash
docker logs project-tracking-redis -f
```

## ⚡ Performance Check

Check application logs để thấy cache working:

```
INFO  DashboardServiceImpl - Calculating dashboard statistics (not from cache)
INFO  DashboardServiceImpl - Calculating dashboard statistics (not from cache)
```

Khi thấy log này → data đang được tính toán (không dùng cache)

Khi KHÔNG thấy log → data được lấy từ cache (fast!) ✨

## 🎯 Next Steps

1. ✅ Áp dụng cache cho thêm các API khác
2. ✅ Monitor cache hit/miss ratio
3. ✅ Tune TTL values dựa trên usage patterns
4. ✅ Set up Redis password cho production

---

**Xem chi tiết tại:** [REDIS_CACHE_README.md](REDIS_CACHE_README.md)
