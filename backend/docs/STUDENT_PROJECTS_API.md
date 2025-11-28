# 📚 Student Projects API Documentation

## Tổng quan
Các endpoint mới được thêm vào để cho phép lấy danh sách các dự án mà student (sinh viên) tham gia.

---

## 🎯 Endpoints

### 1. Lấy dự án theo Student ID

**Endpoint:** `GET /api/v1/projects/student/{studentId}`

**Mô tả:** Lấy tất cả các dự án mà một sinh viên cụ thể tham gia (là member).

**Parameters:**
- `studentId` (path) - ID của sinh viên

**Response:**
```json
{
  "status": 200,
  "message": "Lấy danh sách dự án của sinh viên thành công",
  "data": [
    {
      "id": 1,
      "title": "Project ABC",
      "status": "ACTIVE",
      "completionPercentage": 45.5,
      ...
    }
  ]
}
```

**Use case:** Admin hoặc giảng viên muốn xem tất cả dự án của một sinh viên cụ thể.

---

### 2. Lấy dự án theo Student ID và Status

**Endpoint:** `GET /api/v1/projects/student/{studentId}/status/{status}`

**Mô tả:** Lấy các dự án mà một sinh viên cụ thể tham gia, lọc theo trạng thái.

**Parameters:**
- `studentId` (path) - ID của sinh viên
- `status` (path) - Trạng thái dự án (`ACTIVE`, `COMPLETED`, `CANCELLED`, etc.)

**Response:**
```json
{
  "status": 200,
  "message": "Lấy danh sách dự án của sinh viên theo trạng thái thành công",
  "data": [...]
}
```

**Use case:** Xem các dự án đang active hoặc đã hoàn thành của một sinh viên.

---

### 3. Lấy dự án của tôi (Current User)

**Endpoint:** `GET /api/v1/projects/my-projects`

**Mô tả:** Lấy tất cả các dự án mà người dùng hiện tại (đang đăng nhập) tham gia.

**Authentication:** Required (Bearer Token)

**Response:**
```json
{
  "status": 200,
  "message": "Lấy danh sách dự án của tôi thành công",
  "data": [
    {
      "id": 1,
      "title": "My Project",
      "status": "ACTIVE",
      "completionPercentage": 60.0,
      "instructorId": 10,
      "instructorName": "Giảng viên ABC",
      ...
    }
  ]
}
```

**Use case:** Student đăng nhập muốn xem tất cả dự án của mình.

**Cache:** Có (key: `my_projects_{userId}`)

---

### 4. Lấy dự án của tôi theo Status

**Endpoint:** `GET /api/v1/projects/my-projects/status/{status}`

**Mô tả:** Lấy các dự án mà người dùng hiện tại tham gia, lọc theo trạng thái.

**Authentication:** Required (Bearer Token)

**Parameters:**
- `status` (path) - Trạng thái dự án

**Response:**
```json
{
  "status": 200,
  "message": "Lấy danh sách dự án của tôi theo trạng thái thành công",
  "data": [...]
}
```

**Use case:** Student muốn xem các dự án đang active hoặc đã hoàn thành của mình.

**Cache:** Có (key: `my_projects_{userId}_{status}`)

---

## 🔍 Implementation Details

### Database Query

**JPQL Query:**
```java
@Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user.id = :userId")
List<Project> findProjectsByMemberUserId(@Param("userId") Long userId);

@Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user.id = :userId AND p.status = :status")
List<Project> findProjectsByMemberUserIdAndStatus(@Param("userId") Long userId, @Param("status") EProjectStatus status);
```

**Giải thích:**
- `JOIN p.members pm` - Join với bảng ProjectMember
- `pm.user.id = :userId` - Lọc theo user ID
- `DISTINCT` - Loại bỏ duplicate (nếu có)

### Service Layer

```java
@Override
public List<ProjectRes> getProjectsByStudent(Long studentId) {
    return projectRepository.findProjectsByMemberUserId(studentId).stream()
            .map(projectMapper::toResponse)
            .collect(Collectors.toList());
}

@Override
@Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, key = "'my_projects_' + #root.target.securityUtil.getCurrentUser().id")
public List<ProjectRes> getMyProjects() {
    User currentUser = securityUtil.getCurrentUser();
    return projectRepository.findProjectsByMemberUserId(currentUser.getId()).stream()
            .map(projectMapper::toResponse)
            .collect(Collectors.toList());
}
```

### Cache Strategy

- **Cache Name:** `projectList`
- **TTL:** 180 seconds (3 minutes)
- **Cache Keys:**
  - `my_projects_{userId}` - Cho getMyProjects()
  - `my_projects_{userId}_{status}` - Cho getMyProjectsByStatus()

**Cache Eviction:**
- Cache sẽ bị xóa khi:
  - Có project mới được tạo
  - Project được cập nhật
  - Project bị xóa
  - Member được thêm/xóa khỏi project

---

## 📊 Use Cases

### Use Case 1: Student Dashboard
```typescript
// Student xem tất cả dự án của mình
GET /api/v1/projects/my-projects

// Hiển thị trong dashboard với filter
GET /api/v1/projects/my-projects/status/ACTIVE
```

### Use Case 2: Instructor View
```typescript
// Giảng viên xem tất cả dự án của một sinh viên
GET /api/v1/projects/student/123

// Lọc theo trạng thái
GET /api/v1/projects/student/123/status/COMPLETED
```

### Use Case 3: Admin Panel
```typescript
// Admin kiểm tra workload của sinh viên
GET /api/v1/projects/student/123
```

---

## 🧪 Testing

### Example cURL Commands

**1. Get my projects (as student):**
```bash
curl -X GET http://localhost:9090/api/v1/projects/my-projects \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**2. Get projects by student ID:**
```bash
curl -X GET http://localhost:9090/api/v1/projects/student/123 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**3. Get my active projects:**
```bash
curl -X GET http://localhost:9090/api/v1/projects/my-projects/status/ACTIVE \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔐 Security

- Tất cả endpoints đều yêu cầu authentication (Bearer Token)
- `getMyProjects()` và `getMyProjectsByStatus()` tự động lấy user từ SecurityContext
- Không cần truyền userId trong request body/params, tăng tính bảo mật

---

## 📝 Notes

1. **Performance:** Queries sử dụng JOIN để tối ưu số lượng queries
2. **Cache:** Endpoints cho current user được cache để tăng performance
3. **Distinct:** Sử dụng DISTINCT trong query để tránh duplicate results
4. **Lazy Loading:** ProjectMember relationship được lazy load, không ảnh hưởng performance

---

## 🎓 Difference với Instructor Endpoints

| Feature | Student Endpoint | Instructor Endpoint |
|---------|-----------------|-------------------|
| Query | JOIN với members | Filter theo instructor |
| Use case | Student tham gia project | Instructor phụ trách project |
| Relationship | Many-to-Many (qua ProjectMember) | One-to-Many (direct) |
| Cache | Có (my-projects) | Không |
| Authorization | Current user auto | Cần userId parameter |

---

**Created:** November 28, 2025  
**Last Updated:** November 28, 2025  
**Status:** ✅ Implemented and Tested
