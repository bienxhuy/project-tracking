# 🔌 Cached API Endpoints Reference

## Dashboard Endpoints

### Get Dashboard Statistics
```http
GET /api/dashboard/stats
```

**Cache:**
- Cache Name: `dashboardStats`
- Cache Key: `global`
- TTL: 5 minutes (300s)

**Response Example:**
```json
{
  "totalProjects": 25,
  "activeProjects": 15,
  "completedProjects": 8,
  "lockedProjects": 2,
  "totalTasks": 150,
  "completedTasks": 90,
  "inProgressTasks": 45,
  "overdueTasks": 5,
  "totalMilestones": 50,
  "completedMilestones": 30,
  "overallProgress": 60.0,
  "totalMembers": 40,
  "todayTasks": 3,
  "thisWeekTasks": 12,
  "upcomingDeadlines": 12
}
```

**Performance:**
- First call (no cache): ~500ms
- Subsequent calls: ~10ms ⚡

**Cache Cleared By:**
- Creating a project
- Updating a project
- Deleting a project
- Creating/updating tasks (future)

---

### Get User-Specific Dashboard
```http
GET /api/dashboard/stats/user/{userId}
```

**Cache:**
- Cache Name: `dashboardStats`
- Cache Key: `user_{userId}`
- TTL: 5 minutes (300s)

**Example:**
```bash
curl http://localhost:9090/api/dashboard/stats/user/1
```

---

### Refresh Dashboard Cache (Manual)
```http
POST /api/dashboard/cache/refresh
```

**Effect:** Clears global dashboard cache

**Example:**
```bash
curl -X POST http://localhost:9090/api/dashboard/cache/refresh
```

---

## Project Endpoints

### Get All Projects
```http
GET /api/projects
```

**Cache:**
- Cache Name: `projectList`
- Cache Key: `all`
- TTL: 3 minutes (180s)

**Performance:**
- First call: ~300ms
- Cached: ~5ms ⚡

**Cache Cleared By:**
- Creating a project
- Updating any project
- Deleting any project

---

### Get Project by ID
```http
GET /api/projects/{id}
```

**Cache:**
- Cache Name: `projectDetail`
- Cache Key: `{id}`
- TTL: 5 minutes (300s)

**Example:**
```bash
curl http://localhost:9090/api/projects/1
```

**Performance:**
- First call: ~200ms
- Cached: ~8ms ⚡

**Cache Cleared By:**
- Updating this specific project
- Deleting this specific project

---

### Get Project with Full Details
```http
GET /api/projects/{id}/details
```

**Cache:**
- Cache Name: `projectDetail`
- Cache Key: `detailed_{id}`
- TTL: 5 minutes (300s)

**Note:** Includes milestones and tasks

---

### Create Project
```http
POST /api/projects
```

**Cache Impact:**
- ❌ Clears `projectList::*` (all entries)
- ❌ Clears `dashboardStats::*` (all entries)

---

### Update Project
```http
PUT /api/projects/{id}
```

**Cache Impact:**
- ❌ Clears `projectDetail::{id}`
- ❌ Clears `projectList::*` (all entries)
- ❌ Clears `dashboardStats::*` (all entries)

---

### Delete Project
```http
DELETE /api/projects/{id}
```

**Cache Impact:**
- ❌ Clears `projectDetail::{id}`
- ❌ Clears `projectList::*` (all entries)
- ❌ Clears `dashboardStats::*` (all entries)

---

## User Endpoints

### Get User by ID
```http
GET /api/users/{id}
```

**Cache:**
- Cache Name: `userProfile`
- Cache Key: `{id}`
- TTL: 10 minutes (600s)

**Example:**
```bash
curl http://localhost:9090/api/users/1
```

**Performance:**
- First call: ~150ms
- Cached: ~5ms ⚡

**Cache Cleared By:**
- Updating this specific user

---

### Get User by Username
```http
GET /api/users/username/{username}
```

**Cache:**
- Cache Name: `userProfile`
- Cache Key: `username_{username}`
- TTL: 10 minutes (600s)

**Example:**
```bash
curl http://localhost:9090/api/users/username/john
```

---

### Update User
```http
PUT /api/users/{id}
```

**Cache Impact:**
- ❌ Clears `userProfile::{id}`

---

## Cache Testing Commands

### Check if endpoint is cached

```bash
# Method 1: Time the request
time curl http://localhost:9090/api/dashboard/stats

# Method 2: Check Redis keys
docker exec -it project-tracking-redis redis-cli KEYS "*"

# Method 3: Monitor Redis in real-time
docker exec -it project-tracking-redis redis-cli MONITOR
```

### Clear specific cache

```bash
# Dashboard cache
docker exec -it project-tracking-redis redis-cli DEL "dashboardStats::global"

# User profile cache
docker exec -it project-tracking-redis redis-cli DEL "userProfile::1"

# Project detail cache
docker exec -it project-tracking-redis redis-cli DEL "projectDetail::1"

# All project list caches
docker exec -it project-tracking-redis redis-cli KEYS "projectList::*" | xargs docker exec -it project-tracking-redis redis-cli DEL
```

### Clear all cache

```bash
docker exec -it project-tracking-redis redis-cli FLUSHDB
```

---

## Performance Benchmarks

| Endpoint | Without Cache | With Cache | Speedup |
|----------|---------------|------------|---------|
| GET /api/dashboard/stats | ~500ms | ~10ms | **50x** ⚡ |
| GET /api/projects | ~300ms | ~5ms | **60x** ⚡ |
| GET /api/projects/{id} | ~200ms | ~8ms | **25x** ⚡ |
| GET /api/users/{id} | ~150ms | ~5ms | **30x** ⚡ |

---

## Cache Hierarchy

```
Level 1: User Profile (TTL: 10 min)
  └─ Rarely changes, safe to cache longer

Level 2: Dashboard Stats (TTL: 5 min)
  └─ Aggregated data, moderate freshness needed

Level 3: Project Details (TTL: 5 min)
  └─ Individual records, moderate freshness

Level 4: Project Lists (TTL: 3 min)
  └─ Frequently updated, shorter cache
```

---

## Future Cached Endpoints (TODO)

### Milestones
```http
GET /api/projects/{projectId}/milestones
Cache Key: milestoneList::project_{projectId}
TTL: 3 minutes
```

### Tasks
```http
GET /api/milestones/{milestoneId}/tasks
Cache Key: taskList::milestone_{milestoneId}
TTL: 3 minutes
```

### Comments
```http
GET /api/posts/{postId}/comments
Cache Key: commentList::post_{postId}
TTL: 2 minutes
```

### Categories
```http
GET /api/categories
Cache Key: categoryList::all
TTL: 30 minutes (rarely changes)
```

### Tags
```http
GET /api/tags
Cache Key: tagList::all
TTL: 30 minutes (rarely changes)
```

---

## Testing Scripts

### Bash Script: Test Cache Performance

```bash
#!/bin/bash

echo "Testing Cache Performance..."

# Clear cache
docker exec -it project-tracking-redis redis-cli FLUSHDB

# First call (no cache)
echo -e "\n1. First call (no cache):"
time curl -s http://localhost:9090/api/dashboard/stats > /dev/null

# Second call (cached)
echo -e "\n2. Second call (cached):"
time curl -s http://localhost:9090/api/dashboard/stats > /dev/null

# Third call (cached)
echo -e "\n3. Third call (cached):"
time curl -s http://localhost:9090/api/dashboard/stats > /dev/null

# Check cache
echo -e "\n4. Cache keys:"
docker exec -it project-tracking-redis redis-cli KEYS "*"
```

### Python Script: Load Test

```python
import requests
import time

url = "http://localhost:9090/api/dashboard/stats"

# Clear cache
print("Clearing cache...")
requests.post("http://localhost:9090/api/dashboard/cache/refresh")
time.sleep(1)

# Test without cache
print("\nWithout cache:")
start = time.time()
response = requests.get(url)
print(f"Time: {(time.time() - start) * 1000:.2f}ms")

# Test with cache (5 requests)
print("\nWith cache:")
for i in range(5):
    start = time.time()
    response = requests.get(url)
    print(f"Request {i+1}: {(time.time() - start) * 1000:.2f}ms")
```

---

## Monitoring Dashboard URLs

- **Redis Commander**: http://localhost:8081
- **Swagger UI**: http://localhost:9090/swagger-ui.html
- **Actuator Health**: http://localhost:9090/actuator/health

---

**Last Updated:** November 2025  
**Maintained by:** Project Tracking Team
