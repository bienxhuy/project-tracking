# ✅ Redis Cache Implementation Checklist

## 📋 Setup Checklist

### Phase 1: Environment Setup
- [ ] **Install Docker** (nếu chưa có)
  ```bash
  docker --version
  ```

- [ ] **Start Redis Container**
  ```bash
  cd backend
  docker-compose -f docker-compose.redis.yml up -d
  ```

- [ ] **Verify Redis is running**
  ```bash
  docker ps | grep redis
  # Should see: project-tracking-redis
  ```

- [ ] **Test Redis connection**
  ```bash
  docker exec -it project-tracking-redis redis-cli ping
  # Should return: PONG
  ```

- [ ] **Access Redis Commander** (Optional GUI)
  - Open browser: http://localhost:8081
  - [ ] Can see Redis instance

### Phase 2: Configuration
- [ ] **Copy .env.redis.example to .env**
  ```bash
  cat .env.redis.example >> .env
  ```

- [ ] **Verify .env has Redis config**
  ```properties
  REDIS_HOST=localhost
  REDIS_PORT=6379
  REDIS_PASSWORD=
  ```

- [ ] **Check application.properties**
  - [ ] Redis configuration present
  - [ ] Cache TTL values configured

### Phase 3: Build & Run
- [ ] **Clean and build project**
  ```bash
  mvn clean install
  ```

- [ ] **Check for compilation errors**
  - [ ] No errors in build output

- [ ] **Run application**
  ```bash
  mvn spring-boot:run
  ```

- [ ] **Check application logs**
  - [ ] Look for: "Started ProjectTrackingApplication"
  - [ ] No Redis connection errors

### Phase 4: Testing

#### Basic Cache Test
- [ ] **First API Call (Cache MISS)**
  ```bash
  time curl http://localhost:9090/api/dashboard/stats
  ```
  - [ ] Response time: ~200-500ms
  - [ ] Data returned successfully

- [ ] **Second API Call (Cache HIT)**
  ```bash
  time curl http://localhost:9090/api/dashboard/stats
  ```
  - [ ] Response time: ~5-10ms ⚡
  - [ ] Same data returned

- [ ] **Verify cache in Redis**
  ```bash
  docker exec -it project-tracking-redis redis-cli KEYS "*"
  ```
  - [ ] See key: `dashboardStats::global`

#### Cache Invalidation Test
- [ ] **Get initial stats**
  ```bash
  curl http://localhost:9090/api/dashboard/stats | jq '.totalProjects'
  ```
  - [ ] Note the count

- [ ] **Create a new project** (adjust with valid data)
  ```bash
  curl -X POST http://localhost:9090/api/projects \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer YOUR_TOKEN" \
    -d '{...project data...}'
  ```
  - [ ] Project created successfully

- [ ] **Verify cache was cleared**
  ```bash
  docker exec -it project-tracking-redis redis-cli KEYS "dashboardStats*"
  ```
  - [ ] No keys found (cache cleared)

- [ ] **Get stats again**
  ```bash
  curl http://localhost:9090/api/dashboard/stats | jq '.totalProjects'
  ```
  - [ ] Count increased by 1
  - [ ] New cache created

#### User Profile Cache Test
- [ ] **Get user profile**
  ```bash
  curl http://localhost:9090/api/users/1
  ```
  - [ ] First call slower
  - [ ] Second call faster

- [ ] **Check cache key**
  ```bash
  docker exec -it project-tracking-redis redis-cli KEYS "userProfile*"
  ```
  - [ ] See key: `userProfile::1`

#### Project Cache Test
- [ ] **Get project list**
  ```bash
  curl http://localhost:9090/api/projects
  ```
  - [ ] Cache created: `projectList::all`

- [ ] **Get project detail**
  ```bash
  curl http://localhost:9090/api/projects/1
  ```
  - [ ] Cache created: `projectDetail::1`

### Phase 5: Monitoring

#### Redis Commander
- [ ] **Open Redis Commander**: http://localhost:8081
  - [ ] Can see all cache keys
  - [ ] Can view values
  - [ ] Can see TTL countdown

#### Redis CLI Monitoring
- [ ] **View all keys**
  ```bash
  docker exec -it project-tracking-redis redis-cli KEYS "*"
  ```

- [ ] **Check TTL of a key**
  ```bash
  docker exec -it project-tracking-redis redis-cli TTL "dashboardStats::global"
  ```
  - [ ] Should show remaining seconds (≤300)

- [ ] **Monitor real-time operations**
  ```bash
  docker exec -it project-tracking-redis redis-cli MONITOR
  ```
  - [ ] Make API calls and see Redis commands

- [ ] **Check memory usage**
  ```bash
  docker exec -it project-tracking-redis redis-cli INFO memory
  ```
  - [ ] used_memory should be reasonable

### Phase 6: Performance Validation

- [ ] **Measure average response time without cache**
  ```bash
  # Clear cache first
  docker exec -it project-tracking-redis redis-cli FLUSHDB
  
  # Measure
  for i in {1..5}; do
    time curl -s http://localhost:9090/api/dashboard/stats > /dev/null
  done
  ```
  - [ ] Average: ~200-500ms

- [ ] **Measure average response time with cache**
  ```bash
  # First call to populate cache
  curl -s http://localhost:9090/api/dashboard/stats > /dev/null
  
  # Measure cached calls
  for i in {1..5}; do
    time curl -s http://localhost:9090/api/dashboard/stats > /dev/null
  done
  ```
  - [ ] Average: ~5-10ms ⚡
  - [ ] Performance gain: 20-50x

### Phase 7: Error Handling

- [ ] **Test with Redis down**
  ```bash
  docker-compose -f docker-compose.redis.yml stop
  ```
  - [ ] Application still works (graceful degradation)
  - [ ] Just slower (no cache)

- [ ] **Start Redis again**
  ```bash
  docker-compose -f docker-compose.redis.yml start
  ```
  - [ ] Cache works again

### Phase 8: Documentation Review

- [ ] **Read REDIS_QUICK_START.md**
- [ ] **Read REDIS_CACHE_README.md**
- [ ] **Read REDIS_ARCHITECTURE.md**
- [ ] **Read REDIS_IMPLEMENTATION_SUMMARY.md**
- [ ] **Understand cache strategy**

## 🎯 Production Readiness (Optional)

- [ ] **Set Redis password**
  - [ ] Update docker-compose.redis.yml
  - [ ] Update .env with password
  - [ ] Test connection

- [ ] **Configure memory limits**
  ```bash
  docker exec -it project-tracking-redis redis-cli CONFIG SET maxmemory 256mb
  docker exec -it project-tracking-redis redis-cli CONFIG SET maxmemory-policy allkeys-lru
  ```

- [ ] **Enable persistence**
  - [ ] Verify RDB snapshots
  - [ ] Check AOF if needed

- [ ] **Set up monitoring**
  - [ ] Prometheus metrics
  - [ ] Grafana dashboard
  - [ ] Alert rules

## 🐛 Troubleshooting Checklist

If something doesn't work:

- [ ] **Redis not starting**
  - [ ] Check Docker is running
  - [ ] Check port 6379 is free
  - [ ] Check logs: `docker logs project-tracking-redis`

- [ ] **Application can't connect to Redis**
  - [ ] Verify REDIS_HOST in .env
  - [ ] Check Redis is running
  - [ ] Test connection: `redis-cli ping`

- [ ] **Cache not working**
  - [ ] Check @EnableCaching in config
  - [ ] Verify cache annotations on methods
  - [ ] Check application logs for errors

- [ ] **Cache not clearing**
  - [ ] Verify @CacheEvict annotations
  - [ ] Check method is actually called
  - [ ] Manual clear: `FLUSHDB`

## 📊 Success Criteria

✅ **Setup is successful when:**
1. Redis container is running
2. Application connects to Redis without errors
3. Dashboard API response time < 20ms (after first call)
4. Cache keys appear in Redis Commander
5. Cache is cleared when data changes
6. Performance improvement of 20x or more

## 🎓 Next Steps After Completion

- [ ] Monitor cache hit/miss ratio
- [ ] Tune TTL values based on usage
- [ ] Add cache for more endpoints:
  - [ ] Milestones
  - [ ] Tasks
  - [ ] Comments
  - [ ] Categories
- [ ] Set up production Redis cluster
- [ ] Implement cache warming strategy
- [ ] Add cache metrics to dashboard

---

**✨ Congratulations!** If all checkboxes are ticked, your Redis cache is working perfectly! 🎉

**Need Help?** 
- Check logs: `docker logs project-tracking-redis`
- Read docs: `REDIS_CACHE_README.md`
- Redis Commander: http://localhost:8081
