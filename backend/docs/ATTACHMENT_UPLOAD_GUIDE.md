# Hướng dẫn Upload Attachment cho Report

## Tổng quan

Hệ thống cho phép upload file đính kèm cho Report, Comment, và các entity khác với các ràng buộc sau:

### Loại file được phép
- **PDF**: `application/pdf`
- **DOC**: `application/msword`
- **DOCX**: `application/vnd.openxmlformats-officedocument.wordprocessingml.document`
- **ZIP**: `application/zip`, `application/x-zip-compressed`

### Giới hạn
- **Kích thước tối đa**: 50MB
- **Validation**: Tự động kiểm tra loại file và kích thước trước khi upload

---

## API Endpoints

### 0. Tạo Report với Attachments (Recommended)

**Endpoint**: `POST /api/v1/reports`

**Content-Type**: `multipart/form-data`

**Parameters**:
- `title` (form-data): Tiêu đề report
- `content` (form-data): Nội dung report
- `projectId` (form-data): ID của project
- `milestoneId` (form-data, optional): ID của milestone
- `taskId` (form-data, optional): ID của task
- `attachments` (form-data, optional): Mảng các file đính kèm

**Request Example** (curl):
```bash
curl -X POST http://localhost:8080/api/v1/reports \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "title=Weekly Progress Report" \
  -F "content=This week we completed..." \
  -F "projectId=1" \
  -F "taskId=5" \
  -F "attachments=@/path/to/document1.pdf" \
  -F "attachments=@/path/to/document2.docx"
```

**Response**:
```json
{
  "status": "success",
  "message": "Tạo báo cáo thành công",
  "data": {
    "id": 10,
    "title": "Weekly Progress Report",
    "content": "This week we completed...",
    "status": "DRAFT",
    "projectId": 1,
    "taskId": 5,
    "authorId": 1,
    "authorName": "Nguyen Van A",
    "createdAt": "2025-11-29T10:00:00"
  }
}
```

**Note**: Attachments sẽ được tự động upload và link với report. Để lấy danh sách attachments, sử dụng endpoint `GET /api/v1/attachments/report/{reportId}`

---

### 1. Upload file cho Report (Đơn file)

**Endpoint**: `POST /api/v1/attachments/report/{reportId}`

**Parameters**:
- `reportId` (path): ID của report
- `file` (form-data): File cần upload

**Request Example** (curl):
```bash
curl -X POST http://localhost:8080/api/v1/attachments/report/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/document.pdf"
```

**Response**:
```json
{
  "code": "200",
  "message": "Success",
  "data": {
    "id": 1,
    "fileName": "document.pdf",
    "filePath": "uuid_document.pdf",
    "fileType": "application/pdf",
    "fileSize": 1024000,
    "url": "http://localhost:8080/api/v1/uploads/uuid_document.pdf",
    "projectId": 1,
    "reportId": 1,
    "uploadedById": 1,
    "uploadedByName": "Nguyen Van A",
    "createdAt": "2025-11-29T10:00:00"
  }
}
```

---

### 2. Upload nhiều file cho Report

**Endpoint**: `POST /api/v1/attachments/report/{reportId}/multiple`

**Parameters**:
- `reportId` (path): ID của report
- `files` (form-data): Mảng các file cần upload

**Request Example** (curl):
```bash
curl -X POST http://localhost:8080/api/v1/attachments/report/1/multiple \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "files=@/path/to/document1.pdf" \
  -F "files=@/path/to/document2.docx" \
  -F "files=@/path/to/archive.zip"
```

**Response**:
```json
{
  "code": "200",
  "message": "Success",
  "data": [
    {
      "id": 1,
      "fileName": "document1.pdf",
      "fileType": "application/pdf",
      ...
    },
    {
      "id": 2,
      "fileName": "document2.docx",
      "fileType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      ...
    }
  ]
}
```

---

### 3. Upload file cho Comment

**Endpoint**: `POST /api/v1/attachments/comment/{commentId}`

**Parameters**:
- `commentId` (path): ID của comment
- `file` (form-data): File cần upload

**Request Example**:
```bash
curl -X POST http://localhost:8080/api/v1/attachments/comment/5 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/document.pdf"
```

---

### 4. Upload file với tùy chọn linh hoạt

**Endpoint**: `POST /api/v1/attachments/upload`

**Parameters**:
- `file` (form-data): File cần upload
- `projectId` (form-data, optional): ID của project
- `milestoneId` (form-data, optional): ID của milestone
- `taskId` (form-data, optional): ID của task
- `reportId` (form-data, optional): ID của report
- `commentId` (form-data, optional): ID của comment

**Request Example**:
```bash
curl -X POST http://localhost:8080/api/v1/attachments/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/document.pdf" \
  -F "projectId=1" \
  -F "reportId=5"
```

---

### 5. Lấy danh sách attachments của Report

**Endpoint**: `GET /api/v1/attachments/report/{reportId}`

**Response**:
```json
{
  "code": "200",
  "message": "Success",
  "data": [
    {
      "id": 1,
      "fileName": "document.pdf",
      "fileType": "application/pdf",
      "fileSize": 1024000,
      "url": "http://localhost:8080/api/v1/uploads/uuid_document.pdf",
      ...
    }
  ]
}
```

---

### 6. Lấy thông tin một attachment

**Endpoint**: `GET /api/v1/attachments/{id}`

**Response**:
```json
{
  "code": "200",
  "message": "Success",
  "data": {
    "id": 1,
    "fileName": "document.pdf",
    "fileType": "application/pdf",
    ...
  }
}
```

---

### 7. Xóa attachment

**Endpoint**: `DELETE /api/v1/attachments/{id}`

**Authorization**: Chỉ người upload hoặc admin mới có quyền xóa

**Response**:
```json
{
  "code": "200",
  "message": "Success",
  "data": null
}
```

---

## Error Codes

| Code | Message | Description |
|------|---------|-------------|
| 13001 | Attachment does not exist | Attachment không tồn tại |
| 13002 | Failed to upload file | Lỗi khi upload file |
| 13003 | Failed to delete file | Lỗi khi xóa file |
| 13004 | Invalid file type | Loại file không hợp lệ (chỉ chấp nhận PDF, DOC, DOCX, ZIP) |
| 13005 | File size exceeds maximum limit | File vượt quá 50MB |
| 8001 | Report does not exist | Report không tồn tại |
| 10001 | Comment does not exist | Comment không tồn tại |

---

## Flow sử dụng

### Luồng 1: Tạo Report kèm Attachments (Recommended) ⭐

```
1. User tạo Report với attachments trong 1 request
   POST /api/v1/reports
   Content-Type: multipart/form-data
   
   Form data:
   - title: "Weekly Report"
   - content: "..."
   - projectId: 1
   - taskId: 5
   - attachments: [document1.pdf, document2.docx]
   
   Response: { "id": 10, "title": "Weekly Report", ... }

2. Attachments tự động được upload và link với report

3. Lấy danh sách attachments
   GET /api/v1/attachments/report/10
   
   Response: [
     { "id": 1, "fileName": "document1.pdf", ... },
     { "id": 2, "fileName": "document2.docx", ... }
   ]
```

### Luồng 2: Tạo Report trước, Upload Attachments sau

```
1. User tạo Report (không có attachments)
   POST /api/v1/reports
   Content-Type: application/json
   {
     "title": "Weekly Report",
     "content": "...",
     "projectId": 1,
     "taskId": 5
   }
   
   Response: { "id": 10, ... }

2. User upload attachments cho Report vừa tạo
   POST /api/v1/attachments/report/10/multiple
   files: [document1.pdf, document2.docx]
   
   Response: [
     { "id": 1, "fileName": "document1.pdf", ... },
     { "id": 2, "fileName": "document2.docx", ... }
   ]

3. Khi lấy Report, attachments sẽ được include
   GET /api/v1/reports/10
```

---

## Validation

### Backend Validation
- ✅ Check file empty
- ✅ Check file size (max 50MB)
- ✅ Check file type (PDF, DOC, DOCX, ZIP only)
- ✅ Check report/comment exists
- ✅ Check user authentication

### Frontend Validation (Recommended)
```typescript
const ACCEPTED_FILE_TYPES = [
  "application/pdf",
  "application/msword",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  "application/zip",
];

const MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

function validateFile(file: File): boolean {
  if (!ACCEPTED_FILE_TYPES.includes(file.type)) {
    alert("Chỉ chấp nhận file PDF, DOC, DOCX, ZIP");
    return false;
  }
  
  if (file.size > MAX_FILE_SIZE) {
    alert("File không được vượt quá 50MB");
    return false;
  }
  
  return true;
}
```

---

## Security

1. **Authentication**: Tất cả endpoints yêu cầu Bearer token
2. **Authorization**: 
   - Upload: Bất kỳ user nào đã authenticated
   - Delete: Chỉ người upload hoặc admin
3. **File Storage**: File được lưu với UUID prefix để tránh conflict
4. **File Type Validation**: Backend validate MIME type

---

## Database Schema

```sql
CREATE TABLE attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    url VARCHAR(500),
    project_id BIGINT,
    milestone_id BIGINT,
    task_id BIGINT,
    report_id BIGINT,
    comment_id BIGINT,
    uploaded_by_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES reports(id),
    FOREIGN KEY (comment_id) REFERENCES comments(id),
    FOREIGN KEY (uploaded_by_id) REFERENCES users(id)
);
```

---

## Notes

- File được lưu trong thư mục được cấu hình bởi `UploadPathHolder.uploadDir`
- URL format: `{upload.base-url}/{storedFileName}`
- File name được thêm UUID prefix để đảm bảo unique
- Audit trail được tự động track bởi Hibernate Envers
