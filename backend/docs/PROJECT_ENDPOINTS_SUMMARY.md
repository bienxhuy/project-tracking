# 📚 Project APIs - Summary Documentation

## 🎯 Tổng quan các Endpoints

Hệ thống có **2 nhóm endpoints** để lấy projects:

### 1️⃣ **Endpoints WITH Filters** (Year/Semester/Batch)
- Auto-detect current academic period nếu không truyền params
- Sử dụng cho việc xem projects theo kỳ học cụ thể

### 2️⃣ **Endpoints WITHOUT Filters** (Get ALL)
- Lấy TẤT CẢ projects không giới hạn kỳ học
- Sử dụng khi cần xem toàn bộ lịch sử projects

---

## 📋 Danh sách Endpoints

### Group 1: Student Projects WITH Filters

| Endpoint | Method | Filters | Description |
|----------|--------|---------|-------------|
| `/api/v1/projects/student/{studentId}` | GET | year, semester, batch | Lấy projects của student theo kỳ |
| `/api/v1/projects/student/{studentId}/status/{status}` | GET | year, semester, batch | Lấy projects của student theo status và kỳ |
| `/api/v1/projects/my-projects` | GET | year, semester, batch | Lấy projects của tôi theo kỳ |
| `/api/v1/projects/my-projects/status/{status}` | GET | year, semester, batch | Lấy projects của tôi theo status và kỳ |

### Group 2: Student Projects WITHOUT Filters (Get ALL)

| Endpoint | Method | Filters | Description |
|----------|--------|---------|-------------|
| `/api/v1/projects/student/{studentId}/all` | GET | ❌ None | Lấy TẤT CẢ projects của student |
| `/api/v1/projects/student/{studentId}/all/status/{status}` | GET | ❌ None | Lấy TẤT CẢ projects của student theo status |
| `/api/v1/projects/my-projects/all` | GET | ❌ None | Lấy TẤT CẢ projects của tôi |
| `/api/v1/projects/my-projects/all/status/{status}` | GET | ❌ None | Lấy TẤT CẢ projects của tôi theo status |

### Group 3: Admin/General

| Endpoint | Method | Filters | Description |
|----------|--------|---------|-------------|
| `/api/v1/projects` | GET | ❌ None | Lấy tất cả projects (admin) |
| `/api/v1/projects/filter` | GET | year, semester, batch | Lấy projects với filter |

---

## 🔍 So sánh 2 nhóm endpoints

### WITH Filters (Year/Semester/Batch)

**Endpoint:** `GET /api/v1/projects/my-projects`

**Query Params:** `?year=2025&semester=2&batch=1`

**Use Case:**
- Xem projects của kỳ hiện tại (default)
- Xem projects của một kỳ cụ thể
- Filter dashboard theo kỳ học

**Example:**
```bash
# Kỳ hiện tại (auto-detect)
GET /api/v1/projects/my-projects

# Kỳ 1 năm 2024
GET /api/v1/projects/my-projects?year=2024&semester=1

# Đợt 2 của kỳ hiện tại
GET /api/v1/projects/my-projects?batch=2
```

---

### WITHOUT Filters (Get ALL)

**Endpoint:** `GET /api/v1/projects/my-projects/all`

**Query Params:** ❌ None

**Use Case:**
- Xem toàn bộ lịch sử projects
- Portfolio/CV của student
- Báo cáo tổng quan tất cả projects

**Example:**
```bash
# Lấy TẤT CẢ projects (all years/semesters)
GET /api/v1/projects/my-projects/all

# Lấy TẤT CẢ active projects
GET /api/v1/projects/my-projects/all/status/ACTIVE

# Lấy TẤT CẢ completed projects
GET /api/v1/projects/my-projects/all/status/COMPLETED
```

---

## 📊 Use Cases Chi tiết

### Use Case 1: Student Dashboard (Current Period)

**Requirement:** Hiển thị projects của kỳ hiện tại

**Solution:** Use endpoint WITH filters (auto-detect)

```typescript
// Frontend call
GET /api/v1/projects/my-projects
// Auto returns projects of current year/semester/batch
```

---

### Use Case 2: Student Portfolio (All Time)

**Requirement:** Hiển thị TẤT CẢ projects mà student từng tham gia

**Solution:** Use endpoint WITHOUT filters

```typescript
// Frontend call
GET /api/v1/projects/my-projects/all
// Returns ALL projects across all years/semesters
```

---

### Use Case 3: View Specific Period

**Requirement:** Xem projects của học kỳ 1 năm 2024

**Solution:** Use endpoint WITH filters + params

```typescript
// Frontend call
GET /api/v1/projects/my-projects?year=2024&semester=1
```

---

### Use Case 4: Instructor View All Student Projects

**Requirement:** Giảng viên xem tất cả projects của một student

**Solution:** Use endpoint WITHOUT filters

```typescript
// Frontend call
GET /api/v1/projects/student/123/all
// Returns ALL projects of student 123
```

---

### Use Case 5: Admin Report - Active Projects

**Requirement:** Admin muốn xem tất cả active projects

**Solution:** Use endpoint WITHOUT filters + status

```typescript
// Frontend call
GET /api/v1/projects/my-projects/all/status/ACTIVE
// Or for specific student:
GET /api/v1/projects/student/123/all/status/ACTIVE
```

---

## 🔐 Authentication

Tất cả endpoints đều yêu cầu authentication (Bearer Token)

```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects/all" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 💾 Cache Strategy

### WITH Filters Endpoints
```java
@Cacheable(value = "projectList", 
           key = "'my_projects_' + userId + '_' + year + '_' + semester + '_' + batch")
```

**Cache Key Examples:**
- `my_projects_123_2025_2_1`
- `my_projects_123_2024_1_null`

### WITHOUT Filters Endpoints
```java
@Cacheable(value = "projectList", 
           key = "'all_my_projects_' + userId")
```

**Cache Key Examples:**
- `all_my_projects_123`
- `all_projects_student_123`
- `all_projects_student_123_ACTIVE`

---

## 📝 Response Format

All endpoints return the same response format:

```json
{
  "status": 200,
  "message": "Success message",
  "data": [
    {
      "id": 1,
      "title": "Project ABC",
      "status": "ACTIVE",
      "year": 2025,
      "semester": 2,
      "batch": "1",
      "completionPercentage": 60.5,
      "instructorId": 10,
      "instructorName": "Giảng viên XYZ",
      ...
    }
  ],
  "error": null
}
```

---

## 🧪 Testing Examples

### 1. Get Current Period Projects (WITH Filters - Auto)
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects" \
  -H "Authorization: Bearer TOKEN"
```

### 2. Get Specific Period Projects (WITH Filters - Manual)
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects?year=2024&semester=1" \
  -H "Authorization: Bearer TOKEN"
```

### 3. Get ALL Projects (WITHOUT Filters)
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects/all" \
  -H "Authorization: Bearer TOKEN"
```

### 4. Get ALL Active Projects (WITHOUT Filters + Status)
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects/all/status/ACTIVE" \
  -H "Authorization: Bearer TOKEN"
```

### 5. Get ALL Student Projects (WITHOUT Filters)
```bash
curl -X GET "http://localhost:9090/api/v1/projects/student/123/all" \
  -H "Authorization: Bearer TOKEN"
```

---

## 🎨 Frontend Integration Example

```typescript
// ProjectService.ts

interface ProjectFilters {
  year?: number;
  semester?: number;
  batch?: string;
}

class ProjectService {
  // WITH Filters (current period or specific period)
  async getMyProjects(filters?: ProjectFilters): Promise<Project[]> {
    const params = new URLSearchParams();
    if (filters?.year) params.append('year', filters.year.toString());
    if (filters?.semester) params.append('semester', filters.semester.toString());
    if (filters?.batch) params.append('batch', filters.batch);
    
    const url = `/api/v1/projects/my-projects${params.toString() ? '?' + params : ''}`;
    const response = await fetch(url, { headers: this.getHeaders() });
    return response.json();
  }

  // WITHOUT Filters (all projects)
  async getAllMyProjects(): Promise<Project[]> {
    const response = await fetch('/api/v1/projects/my-projects/all', {
      headers: this.getHeaders()
    });
    return response.json();
  }

  // WITHOUT Filters + Status
  async getAllMyProjectsByStatus(status: string): Promise<Project[]> {
    const response = await fetch(`/api/v1/projects/my-projects/all/status/${status}`, {
      headers: this.getHeaders()
    });
    return response.json();
  }
}

// Usage
const projectService = new ProjectService();

// Dashboard - current period
const currentProjects = await projectService.getMyProjects();

// Dashboard - specific period
const period2024S1 = await projectService.getMyProjects({ year: 2024, semester: 1 });

// Portfolio - all projects
const allProjects = await projectService.getAllMyProjects();

// Filter - all active projects
const activeProjects = await projectService.getAllMyProjectsByStatus('ACTIVE');
```

---

## 📊 Comparison Table

| Feature | WITH Filters | WITHOUT Filters |
|---------|--------------|-----------------|
| **URL Pattern** | `/my-projects` | `/my-projects/all` |
| **Query Params** | ✅ year, semester, batch | ❌ None |
| **Auto-detect** | ✅ Yes (current period) | ❌ N/A |
| **Scope** | Single period or current | All periods |
| **Use Case** | Dashboard, Period view | Portfolio, History |
| **Cache Key** | Includes filters | Excludes filters |
| **Response Size** | ⚡ Smaller (one period) | 📦 Larger (all periods) |

---

## ⚠️ Important Notes

1. **WITH Filters endpoints:**
   - Default to current academic period if no params
   - Useful for dashboards and period-specific views
   - Faster response (smaller dataset)

2. **WITHOUT Filters endpoints:**
   - Return ALL projects across all years/semesters/batches
   - Useful for portfolio, CV, historical views
   - May return larger datasets

3. **Choose the right endpoint:**
   - Dashboard → Use WITH filters (auto or manual)
   - Portfolio → Use WITHOUT filters
   - Reports → Use WITHOUT filters with status filter

---

**Created:** November 28, 2025  
**Last Updated:** November 28, 2025  
**Status:** ✅ Implemented and Tested
