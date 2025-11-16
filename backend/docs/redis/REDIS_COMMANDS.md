# ⚡ Redis Cache - Quick Command Reference

## 🚀 Startup Commands

```bash
# Start Redis with Docker Compose
docker-compose -f docker-compose.redis.yml up -d

# Stop Redis
docker-compose -f docker-compose.redis.yml down

# Restart Redis
docker-compose -f docker-compose.redis.yml restart

# View Redis logs
docker logs project-tracking-redis -f

# Start application
mvn spring-boot:run
```

## 🔍 Monitoring Commands

```bash
# Check Redis is running
docker ps | grep redis

# Test Redis connection
docker exec -it project-tracking-redis redis-cli ping

# Enter Redis CLI
docker exec -it project-tracking-redis redis-cli

# View all cache keys
docker exec -it project-tracking-redis redis-cli KEYS "*"

# Count all keys
docker exec -it project-tracking-redis redis-cli DBSIZE

# Monitor real-time commands
docker exec -it project-tracking-redis redis-cli MONITOR

# Get Redis info
docker exec -it project-tracking-redis redis-cli INFO

# Check memory usage
docker exec -it project-tracking-redis redis-cli INFO memory
```

## 📊 Cache Inspection

```bash
# View specific key value
docker exec -it project-tracking-redis redis-cli GET "dashboardStats::global"

# Check TTL of a key
docker exec -it project-tracking-redis redis-cli TTL "dashboardStats::global"

# View all dashboard cache keys
docker exec -it project-tracking-redis redis-cli KEYS "dashboardStats*"

# View all project cache keys
docker exec -it project-tracking-redis redis-cli KEYS "projectList*"

# View all user profile cache keys
docker exec -it project-tracking-redis redis-cli KEYS "userProfile*"

# Get key type
docker exec -it project-tracking-redis redis-cli TYPE "dashboardStats::global"

# Get key details
docker exec -it project-tracking-redis redis-cli OBJECT ENCODING "dashboardStats::global"
```

## 🗑️ Cache Management

```bash
# Delete specific key
docker exec -it project-tracking-redis redis-cli DEL "dashboardStats::global"

# Delete multiple keys by pattern
docker exec -it project-tracking-redis redis-cli --scan --pattern "projectList*" | xargs docker exec -it project-tracking-redis redis-cli DEL

# Clear all cache (DANGEROUS!)
docker exec -it project-tracking-redis redis-cli FLUSHDB

# Clear all databases (VERY DANGEROUS!)
docker exec -it project-tracking-redis redis-cli FLUSHALL

# Set expiration on key (seconds)
docker exec -it project-tracking-redis redis-cli EXPIRE "dashboardStats::global" 60

# Remove expiration from key
docker exec -it project-tracking-redis redis-cli PERSIST "dashboardStats::global"
```

## 🧪 Testing Commands

```bash
# Test Dashboard API (first call - slow)
time curl http://localhost:9090/api/dashboard/stats

# Test Dashboard API (second call - fast)
time curl http://localhost:9090/api/dashboard/stats

# Refresh dashboard cache via API
curl -X POST http://localhost:9090/api/dashboard/cache/refresh

# Test project list
curl http://localhost:9090/api/projects

# Test user profile
curl http://localhost:9090/api/users/1

# Benchmark (run 10 times)
for i in {1..10}; do time curl -s http://localhost:9090/api/dashboard/stats > /dev/null; done
```

## 📈 Performance Testing

```bash
# AB (Apache Bench) - Install first
# Ubuntu: sudo apt-get install apache2-utils
# macOS: brew install ab

# Test 100 requests, 10 concurrent
ab -n 100 -c 10 http://localhost:9090/api/dashboard/stats

# Test with keep-alive
ab -n 1000 -c 50 -k http://localhost:9090/api/dashboard/stats
```

## 🔧 Configuration Commands

```bash
# Set max memory (256MB)
docker exec -it project-tracking-redis redis-cli CONFIG SET maxmemory 256mb

# Set eviction policy (LRU)
docker exec -it project-tracking-redis redis-cli CONFIG SET maxmemory-policy allkeys-lru

# Get config value
docker exec -it project-tracking-redis redis-cli CONFIG GET maxmemory

# Get all config
docker exec -it project-tracking-redis redis-cli CONFIG GET "*"

# Save config to disk
docker exec -it project-tracking-redis redis-cli CONFIG REWRITE
```

## 📦 Backup & Restore

```bash
# Trigger background save
docker exec -it project-tracking-redis redis-cli BGSAVE

# Get last save time
docker exec -it project-tracking-redis redis-cli LASTSAVE

# Synchronous save (blocks)
docker exec -it project-tracking-redis redis-cli SAVE

# Copy dump.rdb
docker cp project-tracking-redis:/data/dump.rdb ./backup/

# Restore (copy back and restart)
docker cp ./backup/dump.rdb project-tracking-redis:/data/
docker restart project-tracking-redis
```

## 🔐 Security Commands (Production)

```bash
# Set password
docker exec -it project-tracking-redis redis-cli CONFIG SET requirepass "your_strong_password"

# Connect with password
docker exec -it project-tracking-redis redis-cli -a "your_strong_password"

# Disable dangerous commands
docker exec -it project-tracking-redis redis-cli CONFIG SET rename-command FLUSHDB ""
docker exec -it project-tracking-redis redis-cli CONFIG SET rename-command FLUSHALL ""
docker exec -it project-tracking-redis redis-cli CONFIG SET rename-command CONFIG ""
```

## 📊 Statistics & Metrics

```bash
# Get cache hit/miss ratio
docker exec -it project-tracking-redis redis-cli INFO stats | grep keyspace

# Get command statistics
docker exec -it project-tracking-redis redis-cli INFO commandstats

# Get client connections
docker exec -it project-tracking-redis redis-cli CLIENT LIST

# Get slow log
docker exec -it project-tracking-redis redis-cli SLOWLOG GET 10

# Reset stats
docker exec -it project-tracking-redis redis-cli CONFIG RESETSTAT
```

## 🐛 Debugging Commands

```bash
# Check Redis server time
docker exec -it project-tracking-redis redis-cli TIME

# Check latency
docker exec -it project-tracking-redis redis-cli --latency

# Continuous latency monitoring
docker exec -it project-tracking-redis redis-cli --latency-history

# Check if key exists
docker exec -it project-tracking-redis redis-cli EXISTS "dashboardStats::global"

# Scan all keys (safer than KEYS on production)
docker exec -it project-tracking-redis redis-cli SCAN 0

# Debug object
docker exec -it project-tracking-redis redis-cli DEBUG OBJECT "dashboardStats::global"
```

## 🌐 Network Commands

```bash
# Check Redis port
docker port project-tracking-redis

# Test Redis from host
telnet localhost 6379

# Check if Redis is listening
netstat -tuln | grep 6379

# Or using ss
ss -tuln | grep 6379
```

## 📱 One-Liners (Copy & Paste)

```bash
# Complete setup
docker-compose -f docker-compose.redis.yml up -d && sleep 2 && docker exec -it project-tracking-redis redis-cli ping

# View all cache with TTL
docker exec -it project-tracking-redis redis-cli KEYS "*" | while read key; do echo "$key: $(docker exec -it project-tracking-redis redis-cli TTL "$key")s"; done

# Clear all project caches
docker exec -it project-tracking-redis redis-cli KEYS "project*" | xargs docker exec -it project-tracking-redis redis-cli DEL

# Performance test loop
for i in {1..5}; do echo "Test $i:"; time curl -s http://localhost:9090/api/dashboard/stats > /dev/null; done

# Memory usage summary
docker exec -it project-tracking-redis redis-cli INFO memory | grep used_memory_human
```

## 🎯 Common Workflows

### Initial Setup
```bash
cd backend
docker-compose -f docker-compose.redis.yml up -d
docker exec -it project-tracking-redis redis-cli ping
mvn spring-boot:run
```

### Daily Development
```bash
# Morning: Start Redis
docker-compose -f docker-compose.redis.yml start

# Check status
docker ps | grep redis

# Evening: Stop Redis
docker-compose -f docker-compose.redis.yml stop
```

### Debugging Cache Issues
```bash
# 1. Check if Redis is running
docker exec -it project-tracking-redis redis-cli ping

# 2. View all keys
docker exec -it project-tracking-redis redis-cli KEYS "*"

# 3. Monitor real-time
docker exec -it project-tracking-redis redis-cli MONITOR

# 4. Check application logs
tail -f logs/application.log | grep -i cache

# 5. Clear and retry
docker exec -it project-tracking-redis redis-cli FLUSHDB
curl http://localhost:9090/api/dashboard/stats
```

### Performance Analysis
```bash
# Clear cache
docker exec -it project-tracking-redis redis-cli FLUSHDB

# Test without cache
time curl http://localhost:9090/api/dashboard/stats

# Test with cache (3 times)
for i in {1..3}; do time curl -s http://localhost:9090/api/dashboard/stats > /dev/null; done

# Check cache hit rate
docker exec -it project-tracking-redis redis-cli INFO stats | grep keyspace_hits
```

## 🎨 Aliases (Add to ~/.bashrc or ~/.zshrc)

```bash
# Redis shortcuts
alias redis-cli='docker exec -it project-tracking-redis redis-cli'
alias redis-start='docker-compose -f docker-compose.redis.yml up -d'
alias redis-stop='docker-compose -f docker-compose.redis.yml down'
alias redis-logs='docker logs project-tracking-redis -f'
alias redis-keys='docker exec -it project-tracking-redis redis-cli KEYS "*"'
alias redis-clear='docker exec -it project-tracking-redis redis-cli FLUSHDB'
alias redis-stats='docker exec -it project-tracking-redis redis-cli INFO stats'
alias redis-monitor='docker exec -it project-tracking-redis redis-cli MONITOR'

# API testing shortcuts
alias test-dashboard='curl http://localhost:9090/api/dashboard/stats'
alias test-projects='curl http://localhost:9090/api/projects'
alias clear-dashboard='curl -X POST http://localhost:9090/api/dashboard/cache/refresh'
```

## 📚 Quick Reference Table

| Command | Description | Example |
|---------|-------------|---------|
| `PING` | Test connection | `redis-cli PING` |
| `KEYS pattern` | Find keys | `redis-cli KEYS "dashboard*"` |
| `GET key` | Get value | `redis-cli GET "dashboardStats::global"` |
| `SET key value` | Set value | `redis-cli SET mykey "value"` |
| `DEL key` | Delete key | `redis-cli DEL "dashboardStats::global"` |
| `TTL key` | Check TTL | `redis-cli TTL "dashboardStats::global"` |
| `EXPIRE key seconds` | Set TTL | `redis-cli EXPIRE mykey 60` |
| `FLUSHDB` | Clear database | `redis-cli FLUSHDB` |
| `INFO` | Server info | `redis-cli INFO` |
| `MONITOR` | Monitor commands | `redis-cli MONITOR` |
| `DBSIZE` | Count keys | `redis-cli DBSIZE` |

---

**Pro Tip:** Open Redis Commander (http://localhost:8081) for a visual interface! 🎨

**Need Help?** Check `REDIS_CACHE_README.md` for detailed documentation.
