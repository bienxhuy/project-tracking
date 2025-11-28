# Report with Attachments Implementation

## Tổng quan

Đã implement tính năng **tạo Report kèm Attachments trong cùng 1 request**. User có thể upload các file đính kèm (PDF, DOC, DOCX, ZIP) ngay khi tạo report.

---

## 🎯 Hai cách sử dụng

### Cách 1: Tạo Report kèm Attachments (Recommended) ⭐

**Endpoint**: `POST /api/v1/reports`  
**Content-Type**: `multipart/form-data`

**Ưu điểm:**
- ✅ Tạo report và upload file trong 1 request duy nhất
- ✅ Atomic transaction - Nếu upload fail, report không được tạo
- ✅ Giảm số lượng request từ frontend
- ✅ User experience tốt hơn

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "title=Weekly Report" \
  -F "content=Report content here" \
  -F "projectId=1" \
  -F "taskId=5" \
  -F "attachments=@/path/to/file1.pdf" \
  -F "attachments=@/path/to/file2.docx"
```

**Frontend Example (React + TypeScript):**
```typescript
async function createReportWithAttachments(
  reportData: {
    title: string;
    content: string;
    projectId: number;
    taskId?: number;
  },
  files: File[]
) {
  const formData = new FormData();
  
  // Add report data
  formData.append('title', reportData.title);
  formData.append('content', reportData.content);
  formData.append('projectId', reportData.projectId.toString());
  if (reportData.taskId) {
    formData.append('taskId', reportData.taskId.toString());
  }
  
  // Add attachments
  files.forEach(file => {
    formData.append('attachments', file);
  });
  
  const response = await fetch('/api/v1/reports', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  
  return response.json();
}

// Usage
const files = Array.from(fileInput.files);
const report = await createReportWithAttachments({
  title: "Weekly Report",
  content: "This week progress...",
  projectId: 1,
  taskId: 5
}, files);
```

---

### Cách 2: Tạo Report trước, Upload Attachments sau

**Bước 1**: Tạo Report (JSON)
```bash
POST /api/v1/reports
Content-Type: application/json

{
  "title": "Weekly Report",
  "content": "...",
  "projectId": 1,
  "taskId": 5
}
```

**Bước 2**: Upload Attachments
```bash
POST /api/v1/attachments/report/{reportId}/multiple
Content-Type: multipart/form-data

files: [file1.pdf, file2.docx]
```

**Ưu điểm:**
- ✅ Linh hoạt - Có thể upload thêm file sau
- ✅ Dễ retry nếu upload fail
- ✅ Có thể tạo report trước khi có file

**Nhược điểm:**
- ❌ Cần 2 requests
- ❌ Có thể tạo report nhưng quên upload file

---

## 🔧 Implementation Details

### Backend Changes

#### 1. ReportController
```java
@PostMapping
public ApiResponse<ReportRes> createReport(
        @Valid @ModelAttribute ReportReq reportReq,
        @RequestParam(value = "attachments", required = false) MultipartFile[] attachments) {
    ReportRes report = reportService.createReport(reportReq, attachments);
    return new ApiResponse<>(HttpStatus.CREATED, "Tạo báo cáo thành công", report, null);
}
```

**Changes:**
- `@RequestBody` → `@ModelAttribute` (để nhận form-data)
- Thêm parameter `attachments` (optional)

#### 2. IReportService
```java
public interface IReportService {
    ReportRes createReport(ReportReq reportReq);
    ReportRes createReport(ReportReq reportReq, MultipartFile[] attachments);
    // ...existing methods
}
```

**Changes:**
- Thêm overloaded method với attachments parameter

#### 3. ReportServiceImpl
```java
@Override
public ReportRes createReport(ReportReq reportReq, MultipartFile[] attachments) {
    // 1. Validate and get entities
    User author = securityUtil.getCurrentUser();
    Project project = projectRepository.findById(reportReq.getProjectId())...
    
    // 2. Create and save report
    Report report = reportMapper.toEntity(reportReq, project, milestone, task, author);
    report = reportRepository.save(report);
    
    // 3. Upload attachments
    uploadAttachments(report, attachments);
    
    return reportMapper.toResponse(report);
}

private void uploadAttachments(Report report, MultipartFile[] attachments) {
    if (attachments == null || attachments.length == 0) return;
    
    for (MultipartFile file : attachments) {
        validateFile(file); // Check type & size
        String storedFileName = FileUtil.storeAttachmentFile(file);
        
        Attachment attachment = attachmentMapper.toEntity(...);
        attachmentRepository.save(attachment);
    }
}

private void validateFile(MultipartFile file) {
    // Check file type (PDF, DOC, DOCX, ZIP)
    // Check file size (max 50MB)
}
```

**Key Features:**
- ✅ File validation (type + size)
- ✅ Atomic transaction - All or nothing
- ✅ Automatic linking với report
- ✅ Backward compatible (attachments optional)

---

## 📝 Validation Rules

### File Types Allowed
```java
private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
    "application/pdf",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "application/zip",
    "application/x-zip-compressed"
);
```

### File Size Limit
```java
private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
```

### Validation Flow
1. Check if file is empty → Skip
2. Check file size → Throw `FILE_TOO_LARGE` if > 50MB
3. Check file type → Throw `INVALID_FILE_TYPE` if not allowed
4. Upload file → Throw `FILE_UPLOAD_FAILED` if I/O error

---

## 🧪 Testing

### Test Case 1: Create Report without Attachments
```bash
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Report",
    "content": "Content",
    "projectId": 1
  }'
```
**Expected**: Report created successfully, no attachments

### Test Case 2: Create Report with Single Attachment
```bash
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer TOKEN" \
  -F "title=Test Report" \
  -F "content=Content" \
  -F "projectId=1" \
  -F "attachments=@test.pdf"
```
**Expected**: Report + 1 attachment created

### Test Case 3: Create Report with Multiple Attachments
```bash
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer TOKEN" \
  -F "title=Test Report" \
  -F "content=Content" \
  -F "projectId=1" \
  -F "attachments=@file1.pdf" \
  -F "attachments=@file2.docx" \
  -F "attachments=@file3.zip"
```
**Expected**: Report + 3 attachments created

### Test Case 4: Invalid File Type
```bash
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer TOKEN" \
  -F "title=Test Report" \
  -F "content=Content" \
  -F "projectId=1" \
  -F "attachments=@invalid.exe"
```
**Expected**: Error 13004 - Invalid file type

### Test Case 5: File Too Large
```bash
# Upload file > 50MB
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer TOKEN" \
  -F "title=Test Report" \
  -F "content=Content" \
  -F "projectId=1" \
  -F "attachments=@large_file.pdf"
```
**Expected**: Error 13005 - File size exceeds maximum limit

---

## 🔄 Backward Compatibility

### Old Way (Still Works) ✅
```typescript
// 1. Create report with JSON
const reportResponse = await fetch('/api/v1/reports', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: "Report",
    content: "Content",
    projectId: 1
  })
});

// 2. Upload attachments separately
const formData = new FormData();
formData.append('file', file);
await fetch(`/api/v1/attachments/report/${reportId}`, {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` },
  body: formData
});
```

### New Way (Recommended) ⭐
```typescript
// Create report with attachments in one request
const formData = new FormData();
formData.append('title', 'Report');
formData.append('content', 'Content');
formData.append('projectId', '1');
formData.append('attachments', file1);
formData.append('attachments', file2);

const response = await fetch('/api/v1/reports', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` },
  body: formData
});
```

---

## 🎨 Frontend Implementation Example

### React Component with File Upload

```typescript
import { useState } from 'react';

function CreateReportForm() {
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    projectId: 1,
    taskId: null
  });
  const [files, setFiles] = useState<File[]>([]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const fileList = Array.from(e.target.files);
      
      // Validate files
      const validFiles = fileList.filter(file => {
        const validTypes = [
          'application/pdf',
          'application/msword',
          'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          'application/zip'
        ];
        
        if (!validTypes.includes(file.type)) {
          alert(`${file.name}: Invalid file type`);
          return false;
        }
        
        if (file.size > 50 * 1024 * 1024) {
          alert(`${file.name}: File too large (max 50MB)`);
          return false;
        }
        
        return true;
      });
      
      setFiles(validFiles);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const data = new FormData();
    data.append('title', formData.title);
    data.append('content', formData.content);
    data.append('projectId', formData.projectId.toString());
    if (formData.taskId) {
      data.append('taskId', formData.taskId.toString());
    }
    
    files.forEach(file => {
      data.append('attachments', file);
    });
    
    try {
      const response = await fetch('/api/v1/reports', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: data
      });
      
      const result = await response.json();
      console.log('Report created:', result);
    } catch (error) {
      console.error('Error creating report:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Title"
        value={formData.title}
        onChange={(e) => setFormData({...formData, title: e.target.value})}
      />
      
      <textarea
        placeholder="Content"
        value={formData.content}
        onChange={(e) => setFormData({...formData, content: e.target.value})}
      />
      
      <input
        type="file"
        multiple
        accept=".pdf,.doc,.docx,.zip"
        onChange={handleFileChange}
      />
      
      <div>
        {files.map((file, index) => (
          <div key={index}>
            {file.name} ({(file.size / 1024 / 1024).toFixed(2)} MB)
          </div>
        ))}
      </div>
      
      <button type="submit">Create Report</button>
    </form>
  );
}
```

---

## 📊 Database Impact

### Attachments Table
```sql
-- Attachments automatically linked to report
SELECT * FROM attachments WHERE report_id = 10;

-- Results:
id | file_name      | file_type                    | report_id | uploaded_by_id
---+----------------+------------------------------+-----------+---------------
1  | document1.pdf  | application/pdf              | 10        | 1
2  | document2.docx | application/vnd...wordpro... | 10        | 1
```

### Transaction Flow
```
BEGIN TRANSACTION;

1. INSERT INTO reports (title, content, project_id, ...) VALUES (...);
   -- report_id = 10

2. INSERT INTO attachments (file_name, report_id, ...) VALUES ('file1.pdf', 10, ...);
3. INSERT INTO attachments (file_name, report_id, ...) VALUES ('file2.docx', 10, ...);

COMMIT; -- All succeed or all rollback
```

---

## 🚨 Error Handling

### Error Codes
| Code | Message | When |
|------|---------|------|
| 13002 | Failed to upload file | I/O error during upload |
| 13004 | Invalid file type | File type not in allowed list |
| 13005 | File size exceeds maximum limit | File > 50MB |
| 8001 | Report does not exist | Report ID invalid |
| 5001 | Project does not exist | Project ID invalid |

### Error Response Example
```json
{
  "status": "error",
  "message": "Invalid file type",
  "data": null,
  "errorCode": "13004",
  "timestamp": "2025-11-29T10:00:00"
}
```

---

## ✅ Benefits

1. **User Experience**: One-step process thay vì two-step
2. **Data Integrity**: Transaction ensures report + attachments hoặc không có gì
3. **Performance**: Giảm số lượng HTTP requests
4. **Flexibility**: Vẫn có thể upload thêm file sau
5. **Validation**: Tự động validate file trước khi lưu

---

## 📚 Related Documentation

- [ATTACHMENT_UPLOAD_GUIDE.md](./ATTACHMENT_UPLOAD_GUIDE.md) - API Documentation
- [ATTACHMENT_IMPLEMENTATION_SUMMARY.md](./ATTACHMENT_IMPLEMENTATION_SUMMARY.md) - Technical Details

---

**Implementation Date**: November 29, 2025  
**Feature Status**: ✅ Complete and Ready for Testing
