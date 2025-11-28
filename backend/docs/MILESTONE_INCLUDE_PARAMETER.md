# Milestone API - Include Parameter Implementation

## Tổng quan

Đã implement query parameter `include` cho Milestone API để có thể linh hoạt load related entities (tasks) khi cần thiết.

## Thay đổi

### 1. **MilestoneRes DTO** (`dto/res/MilestoneRes.java`)

**Thêm:**
- `@JsonInclude(JsonInclude.Include.NON_NULL)` - Không trả về field null
- `tasksTotal: Integer` - Tổng số tasks
- `tasksCompleted: Integer` - Số tasks hoàn thành
- `tasks: List<TaskRes>` - Danh sách tasks (optional)

**Xóa:**
- `totalReports` (không sử dụng trong spec mới)

### 2. **MilestoneController** (`controller/MilestoneController.java`)

**Cập nhật endpoints:**

#### GET `/api/v1/milestones/{id}`
```java
@GetMapping("/{id}")
public ApiResponse<MilestoneRes> getMilestoneById(
    @PathVariable Long id,
    @RequestParam(required = false) String include
)
```

**Ví dụ:**
- `/api/v1/milestones/1` → Milestone cơ bản (không có tasks)
- `/api/v1/milestones/1?include=tasks` → Milestone với tasks

#### GET `/api/v1/milestones/project/{projectId}`
```java
@GetMapping("/project/{projectId}")
public ApiResponse<List<MilestoneRes>> getMilestonesByProject(
    @PathVariable Long projectId,
    @RequestParam(required = false) String include
)
```

**Ví dụ:**
- `/api/v1/milestones/project/1` → Milestones cơ bản
- `/api/v1/milestones/project/1?include=tasks` → Milestones với tasks

### 3. **IMilestoneService** (`service/IMilestoneService.java`)

**Thay đổi method signatures:**
```java
MilestoneRes getMilestoneById(Long id, String include);
List<MilestoneRes> getMilestonesByProject(Long projectId, String include);
```

### 4. **MilestoneServiceImpl** (`service/impl/MilestoneServiceImpl.java`)

**Logic xử lý `include` parameter:**

```java
@Override
public MilestoneRes getMilestoneById(Long id, String include) {
    boolean includeTasks = include != null && include.contains("tasks");
    
    if (includeTasks) {
        // Load với JOIN FETCH tasks
        milestone = milestoneRepository.findByIdWithTasks(id);
        return milestoneMapper.toResponseWithTasks(milestone);
    } else {
        // Load basic, không JOIN FETCH
        milestone = milestoneRepository.findById(id);
        return milestoneMapper.toResponse(milestone);
    }
}
```

### 5. **MilestoneRepository** (`repository/MilestoneRepository.java`)

**Thêm queries mới:**

```java
@Query("SELECT m FROM Milestone m LEFT JOIN FETCH m.tasks WHERE m.id = :id")
Optional<Milestone> findByIdWithTasks(@Param("id") Long id);

@Query("SELECT DISTINCT m FROM Milestone m LEFT JOIN FETCH m.tasks WHERE m.project.id = :projectId ORDER BY m.orderNumber ASC")
List<Milestone> findByProjectIdWithTasks(@Param("projectId") Long projectId);
```

### 6. **MilestoneMapper** (`mapper/MilestoneMapper.java`)

**Thêm method mapping mới:**

```java
// Trả về basic milestone (không có tasks)
@Named("toResponse")
@Mapping(target = "tasks", ignore = true)
MilestoneRes toResponse(Milestone milestone);

// Trả về milestone với tasks - default method
default MilestoneRes toResponseWithTasks(Milestone milestone) {
    MilestoneRes response = toResponse(milestone);
    if (milestone.getTasks() != null) {
        response.setTasks(milestone.getTasks().stream()
            .map(this::mapTask)
            .collect(Collectors.toList()));
    }
    return response;
}

// Helper method to map Task
@Mapping(...)
TaskRes mapTask(Task task);
```

**Cập nhật statistics:**
- `tasksTotal` - Đếm từ `milestone.getTasks().size()`
- `tasksCompleted` - Đếm tasks có status = COMPLETED

**Fix ambiguous mapping:**
- Thêm `@Named("toResponse")` để ProjectMapper có thể sử dụng qualifier
- `toResponseWithTasks()` là default method, không gây conflict với MapStruct

### 7. **Lock/Unlock Endpoint**

**Đã thay đổi từ 2 methods riêng biệt thành 1 endpoint toggle:**

**Trước:**
```java
void lockMilestone(Long id, Long userId);
void unlockMilestone(Long id);
```

**Sau:**
```java
MilestoneRes toggleMilestoneLock(Long id, Boolean isLocked);
```

**Controller Endpoint:**
```java
@PatchMapping("/{id}/lock")
public ApiResponse<MilestoneRes> toggleMilestoneLock(
    @PathVariable Long id,
    @RequestBody Map<String, Boolean> request
)
```

**Request Body:**
```json
{
  "isLocked": true  // hoặc false
}
```

**Response:** Trả về `MilestoneRes` đã cập nhật

**Logic:**
- Nếu `isLocked = true`: Set locked = true, lockedBy = currentUser, lockedAt = now
- Nếu `isLocked = false`: Set locked = false, lockedBy = null, lockedAt = null

## Cách sử dụng

### Frontend - Axios Example

```typescript
// Basic milestone (không load tasks)
const response = await axios.get(`/api/v1/milestones/${id}`);

// Milestone với tasks
const response = await axios.get(`/api/v1/milestones/${id}?include=tasks`);

// Milestones của project
const response = await axios.get(`/api/v1/milestones/project/${projectId}`);

// Milestones với tasks của project
const response = await axios.get(`/api/v1/milestones/project/${projectId}?include=tasks`);

// Lock milestone
const response = await axios.patch(`/api/v1/milestones/${id}/lock`, {
  isLocked: true
});

// Unlock milestone
const response = await axios.patch(`/api/v1/milestones/${id}/lock`, {
  isLocked: false
});
```

### Response Structure

**Without `include=tasks`:**
```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "title": "Milestone 1",
    "description": "...",
    "tasksTotal": 5,
    "tasksCompleted": 3,
    "completionPercentage": 60.0
    // tasks field is null/not included
  }
}
```

**With `include=tasks`:**
```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "title": "Milestone 1",
    "description": "...",
    "tasksTotal": 5,
    "tasksCompleted": 3,
    "completionPercentage": 60.0,
    "tasks": [
      {
        "id": 1,
        "title": "Task 1",
        "status": "COMPLETED",
        ...
      },
      {
        "id": 2,
        "title": "Task 2",
        "status": "IN_PROGRESS",
        ...
      }
    ]
  }
}
```

## Lợi ích

✅ **Performance tốt hơn:** Chỉ load tasks khi cần
✅ **Flexible:** Frontend control được khi nào cần load tasks
✅ **Giảm bandwidth:** Response nhỏ hơn khi không cần tasks
✅ **Dễ maintain:** Không cần nhiều DTO (MilestoneDetail)
✅ **Scalable:** Dễ thêm include parameter khác (e.g., "reports", "comments")

## Performance Tips

1. **Luôn dùng JOIN FETCH** trong query khi include=tasks để tránh N+1 problem
2. **Frontend nên cache** milestone data để tránh gọi API nhiều lần
3. **Chỉ include tasks khi cần hiển thị chi tiết** milestone, không dùng cho list view
4. **Pagination cho tasks** nếu 1 milestone có quá nhiều tasks (implement sau nếu cần)

## Breaking Changes

⚠️ **Cần cập nhật frontend:**
- Method signature đã thay đổi: `getMilestoneById(id)` → `getMilestoneById(id, include)`
- Method signature đã thay đổi: `getMilestonesByProject(projectId)` → `getMilestonesByProject(projectId, include)`
- Field `totalReports` đã đổi thành `tasksTotal` và `tasksCompleted`
- **Lock/Unlock endpoint riêng:** `PATCH /api/v1/milestones/{id}/lock` với request body `{"isLocked": true/false}`

## API Endpoints Summary

### GET `/api/v1/milestones/{id}`
- **Query Params:** `include=tasks` (optional)
- **Response:** `MilestoneRes` (with or without tasks)

### GET `/api/v1/milestones/project/{projectId}`
- **Query Params:** `include=tasks` (optional)  
- **Response:** `List<MilestoneRes>` (with or without tasks)

### PATCH `/api/v1/milestones/{id}/lock`
- **Description:** Lock/unlock milestone (Instructor only)
- **Request Body:**
```json
{
  "isLocked": true  // or false to unlock
}
```
- **Response:** `MilestoneRes`

## Testing

```bash
# Test basic milestone
curl -X GET "http://localhost:8080/api/v1/milestones/1"

# Test with tasks
curl -X GET "http://localhost:8080/api/v1/milestones/1?include=tasks"

# Test project milestones
curl -X GET "http://localhost:8080/api/v1/milestones/project/1?include=tasks"

# Test lock milestone
curl -X PATCH "http://localhost:8080/api/v1/milestones/1/lock" \
  -H "Content-Type: application/json" \
  -d '{"isLocked": true}'

# Test unlock milestone
curl -X PATCH "http://localhost:8080/api/v1/milestones/1/lock" \
  -H "Content-Type: application/json" \
  -d '{"isLocked": false}'
```

## Future Enhancements

Có thể mở rộng để support nhiều include values:
```
?include=tasks,reports,comments
```

Implementation đã sẵn sàng cho việc này (dùng `include.contains("tasks")`).
