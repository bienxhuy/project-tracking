# 📅 Academic Year Filter API Documentation

## Tổng quan
Tài liệu mô tả cách sử dụng các filter parameters (year, semester, batch) cho các endpoints lấy danh sách projects.

---

## 🎓 Academic Calendar Logic

### Year
- Năm học bắt đầu từ tháng 1 (January)
- Ví dụ: 2025 (năm hiện tại)

### Semester (Học kỳ)
Mỗi năm có **2 học kỳ**:

| Semester | Tháng | Mô tả |
|----------|-------|-------|
| **1** | 1-6 (Jan-Jun) | Học kỳ 1 |
| **2** | 7-12 (Jul-Dec) | Học kỳ 2 |

### Batch (Đợt)
Mỗi học kỳ có **2 đợt (batch)**:

| Semester | Batch | Tháng | Mô tả |
|----------|-------|-------|-------|
| 1 | 1 | 1-3 (Jan-Mar) | Học kỳ 1 - Đợt 1 |
| 1 | 2 | 4-6 (Apr-Jun) | Học kỳ 1 - Đợt 2 |
| 2 | 1 | 7-9 (Jul-Sep) | Học kỳ 2 - Đợt 1 |
| 2 | 2 | 10-12 (Oct-Dec) | Học kỳ 2 - Đợt 2 |

### Auto-Detection
**Nếu không truyền params**, hệ thống tự động sử dụng giá trị hiện tại dựa trên ngày hôm nay:

```java
// Ví dụ: Ngày 28 tháng 11 năm 2025
year = 2025       // Current year
semester = 2      // Tháng 11 thuộc học kỳ 2 (Jul-Dec)
batch = "2"       // Tháng 11 thuộc đợt 2 (Oct-Dec)
```

---

## 🔍 Query Parameters

### Common Parameters
Tất cả các endpoints student projects đều hỗ trợ 3 params:

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | ❌ No | Current year | Năm học (e.g., 2025) |
| `semester` | Integer | ❌ No | Current semester | Học kỳ (1 hoặc 2) |
| `batch` | String | ❌ No | Current batch | Đợt ("1" hoặc "2") |

**Validation:**
- `semester`: Chỉ chấp nhận 1 hoặc 2
- `batch`: Chỉ chấp nhận "1" hoặc "2"
- `year`: Số nguyên dương

---

## 📍 Updated Endpoints

### 1. Get Projects by Student (with filters)

**Endpoint:** `GET /api/v1/projects/student/{studentId}`

**Query Parameters:**
```
year?: number (optional)
semester?: number (optional - 1 or 2)
batch?: string (optional - "1" or "2")
```

**Examples:**

```bash
# Lấy projects của student trong học kỳ hiện tại (auto-detect)
GET /api/v1/projects/student/123

# Lấy projects của năm 2024, học kỳ 1
GET /api/v1/projects/student/123?year=2024&semester=1

# Lấy projects của năm 2025, học kỳ 2, đợt 1
GET /api/v1/projects/student/123?year=2025&semester=2&batch=1

# Chỉ filter theo batch
GET /api/v1/projects/student/123?batch=2
```

**Response:**
```json
{
  "status": 200,
  "message": "Lấy danh sách dự án của sinh viên thành công",
  "data": [
    {
      "id": 1,
      "title": "Project ABC",
      "year": 2025,
      "semester": 2,
      "batch": "1",
      ...
    }
  ]
}
```

---

### 2. Get Projects by Student and Status (with filters)

**Endpoint:** `GET /api/v1/projects/student/{studentId}/status/{status}`

**Query Parameters:**
```
year?: number
semester?: number
batch?: string
```

**Examples:**

```bash
# Lấy active projects của student trong kỳ hiện tại
GET /api/v1/projects/student/123/status/ACTIVE

# Lấy completed projects của năm 2024
GET /api/v1/projects/student/123/status/COMPLETED?year=2024

# Lấy active projects của học kỳ 1, đợt 2
GET /api/v1/projects/student/123/status/ACTIVE?semester=1&batch=2
```

---

### 3. Get My Projects (with filters)

**Endpoint:** `GET /api/v1/projects/my-projects`

**Authentication:** Required (Bearer Token)

**Query Parameters:**
```
year?: number
semester?: number
batch?: string
```

**Examples:**

```bash
# Lấy projects của tôi trong kỳ hiện tại (mặc định)
GET /api/v1/projects/my-projects

# Lấy projects của tôi năm 2025, học kỳ 1
GET /api/v1/projects/my-projects?year=2025&semester=1

# Lấy projects của tôi ở đợt 2
GET /api/v1/projects/my-projects?batch=2
```

**Use Case:**
```typescript
// Student dashboard - hiển thị projects hiện tại
fetch('/api/v1/projects/my-projects')

// Xem projects của học kỳ trước
fetch('/api/v1/projects/my-projects?year=2024&semester=2')
```

---

### 4. Get My Projects by Status (with filters)

**Endpoint:** `GET /api/v1/projects/my-projects/status/{status}`

**Authentication:** Required (Bearer Token)

**Query Parameters:**
```
year?: number
semester?: number
batch?: string
```

**Examples:**

```bash
# Lấy active projects của tôi (kỳ hiện tại)
GET /api/v1/projects/my-projects/status/ACTIVE

# Lấy completed projects năm 2024
GET /api/v1/projects/my-projects/status/COMPLETED?year=2024

# Lấy active projects học kỳ 2
GET /api/v1/projects/my-projects/status/ACTIVE?semester=2
```

---

### 5. Get All Projects with Filters (NEW)

**Endpoint:** `GET /api/v1/projects/filter`

**Authentication:** Required (Bearer Token)

**Query Parameters:**
```
year?: number
semester?: number
batch?: string
```

**Examples:**

```bash
# Lấy tất cả projects của kỳ hiện tại
GET /api/v1/projects/filter

# Lấy tất cả projects năm 2025, học kỳ 1
GET /api/v1/projects/filter?year=2025&semester=1

# Lấy tất cả projects đợt 2
GET /api/v1/projects/filter?batch=2
```

**Use Case:** Admin hoặc instructor xem tất cả projects theo kỳ học.

---

## 💾 Database Queries

### JPQL with Optional Filters

```java
@Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user.id = :userId " +
       "AND (:year IS NULL OR p.year = :year) " +
       "AND (:semester IS NULL OR p.semester = :semester) " +
       "AND (:batch IS NULL OR p.batch = :batch)")
List<Project> findProjectsByMemberUserIdWithFilters(
    @Param("userId") Long userId,
    @Param("year") Integer year,
    @Param("semester") Integer semester,
    @Param("batch") String batch);
```

**Cách hoạt động:**
- Nếu `year = null` → Không filter theo year (lấy tất cả)
- Nếu `year = 2025` → Chỉ lấy projects năm 2025
- Tương tự với semester và batch

---

## 🔧 Implementation Details

### AcademicYearUtil Class

```java
public class AcademicYearUtil {
    public static Integer getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static Integer getCurrentSemester() {
        int month = LocalDate.now().getMonthValue();
        return (month >= 1 && month <= 6) ? 1 : 2;
    }

    public static String getCurrentBatch() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 1 && month <= 3) return "1";
        if (month >= 4 && month <= 6) return "2";
        if (month >= 7 && month <= 9) return "1";
        return "2"; // months 10-12
    }
}
```

### Service Layer Logic

```java
@Override
public List<ProjectRes> getMyProjects(Integer year, Integer semester, String batch) {
    User currentUser = securityUtil.getCurrentUser();
    
    // Auto-detect if not provided
    Integer effectiveYear = year != null ? year : AcademicYearUtil.getCurrentYear();
    Integer effectiveSemester = semester != null ? semester : AcademicYearUtil.getCurrentSemester();
    String effectiveBatch = batch != null ? batch : AcademicYearUtil.getCurrentBatch();

    return projectRepository.findProjectsByMemberUserIdWithFilters(
        currentUser.getId(), effectiveYear, effectiveSemester, effectiveBatch
    ).stream()
        .map(projectMapper::toResponse)
        .collect(Collectors.toList());
}
```

---

## 🎯 Use Cases

### Use Case 1: Student Dashboard (Current Period)
```typescript
// Không truyền params → Tự động lấy kỳ hiện tại
GET /api/v1/projects/my-projects

// Tự động filter theo:
// - year: 2025
// - semester: 2 (tháng 11 = học kỳ 2)
// - batch: "2" (tháng 11 = đợt 2)
```

### Use Case 2: View Previous Semester
```typescript
// Xem projects học kỳ trước
GET /api/v1/projects/my-projects?year=2025&semester=1
```

### Use Case 3: View Specific Batch
```typescript
// Xem projects đợt 1 của học kỳ hiện tại
GET /api/v1/projects/my-projects?batch=1
```

### Use Case 4: Admin Report
```typescript
// Admin xem tất cả projects năm 2024
GET /api/v1/projects/filter?year=2024

// Admin xem projects học kỳ 2
GET /api/v1/projects/filter?semester=2
```

---

## 📊 Cache Strategy

Cache keys bao gồm tất cả filter parameters:

```java
@Cacheable(value = "projectList", 
           key = "'my_projects_' + #userId + '_' + #year + '_' + #semester + '_' + #batch")
```

**Examples:**
- `my_projects_123_2025_2_2` - User 123, năm 2025, học kỳ 2, đợt 2
- `my_projects_123_2024_1_null` - User 123, năm 2024, học kỳ 1, không filter batch
- `all_projects_2025_2_1` - Tất cả projects năm 2025, học kỳ 2, đợt 1

---

## 🧪 Testing Examples

### cURL Commands

**1. Get my projects (current period - auto):**
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**2. Get my projects for specific year:**
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects?year=2024" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**3. Get my projects for semester 1:**
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects?semester=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**4. Get my projects for specific period:**
```bash
curl -X GET "http://localhost:9090/api/v1/projects/my-projects?year=2025&semester=2&batch=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**5. Get all projects with filters:**
```bash
curl -X GET "http://localhost:9090/api/v1/projects/filter?year=2025&semester=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📝 Frontend Integration

### React/TypeScript Example

```typescript
interface ProjectFilters {
  year?: number;
  semester?: number;
  batch?: string;
}

async function getMyProjects(filters?: ProjectFilters) {
  const params = new URLSearchParams();
  
  if (filters?.year) params.append('year', filters.year.toString());
  if (filters?.semester) params.append('semester', filters.semester.toString());
  if (filters?.batch) params.append('batch', filters.batch);
  
  const url = `/api/v1/projects/my-projects${params.toString() ? '?' + params.toString() : ''}`;
  
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return response.json();
}

// Usage
getMyProjects(); // Current period (auto-detect)
getMyProjects({ year: 2024, semester: 1 }); // Specific period
getMyProjects({ batch: "2" }); // Current year/semester, batch 2
```

### Filter Component Example

```tsx
function ProjectFilterSelector() {
  const [filters, setFilters] = useState({
    year: new Date().getFullYear(),
    semester: undefined,
    batch: undefined
  });

  const handleFilterChange = (key: string, value: any) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  return (
    <div>
      <select onChange={(e) => handleFilterChange('year', e.target.value)}>
        <option value={2025}>2025</option>
        <option value={2024}>2024</option>
      </select>
      
      <select onChange={(e) => handleFilterChange('semester', e.target.value)}>
        <option value="">All Semesters</option>
        <option value="1">Semester 1</option>
        <option value="2">Semester 2</option>
      </select>
      
      <select onChange={(e) => handleFilterChange('batch', e.target.value)}>
        <option value="">All Batches</option>
        <option value="1">Batch 1</option>
        <option value="2">Batch 2</option>
      </select>
    </div>
  );
}
```

---

## ⚠️ Important Notes

1. **Default Behavior:** Khi không truyền params, hệ thống tự động filter theo kỳ hiện tại
2. **Partial Filtering:** Có thể truyền chỉ một hoặc hai params (e.g., chỉ year)
3. **Cache:** Mỗi combination của filters tạo ra một cache key riêng
4. **Performance:** Queries được tối ưu với DISTINCT và JOIN
5. **Validation:** Semester chỉ chấp nhận 1 hoặc 2, batch chỉ chấp nhận "1" hoặc "2"

---

**Created:** November 28, 2025  
**Last Updated:** November 28, 2025  
**Status:** ✅ Implemented and Tested
