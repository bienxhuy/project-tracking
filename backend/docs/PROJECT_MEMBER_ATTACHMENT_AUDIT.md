# ProjectMember & Attachment Audit Tracking

## 📋 Overview

Audit tracking has been added to:
- **ProjectMember** - Track role changes, member additions/removals
- **Attachment** - Track file uploads, deletions, metadata changes

## 🎯 Use Cases

### ProjectMember Audit

#### 1. **Track Role Changes**
```
Problem: Who promoted student to team leader?
Solution: View ProjectMember audit history
```

#### 2. **Track Member Additions**
```
Problem: When was this student added to the project?
Solution: Check INSERT revision in audit history
```

#### 3. **Track Member Removals**
```
Problem: Who removed this member from the project?
Solution: Check DELETE revision with username
```

#### 4. **Track Active Status Changes**
```
Problem: When was this member deactivated?
Solution: View isActive field changes in audit
```

### Attachment Audit

#### 1. **Track File Uploads**
```
Problem: Who uploaded this document?
Solution: Check attachment audit history for INSERT revision
```

#### 2. **Track File Deletions**
```
Problem: Who deleted the project report?
Solution: Check DELETE revision with username and timestamp
```

#### 3. **Track File Replacements**
```
Problem: What was the old file before replacement?
Solution: View attachment history to see original filePath
```

## 🚀 API Usage

### ProjectMember History

```bash
# Get all changes to a project member
GET /api/audit/project-members/{projectMemberId}/history
Authorization: Bearer TOKEN

# Example Response
[
  {
    "revisionNumber": 1,
    "timestamp": "2024-01-10T09:00:00",
    "username": "instructor@example.com",
    "ipAddress": "192.168.1.50",
    "revisionType": "INSERT",
    "entityData": {
      "id": 1,
      "role": "STUDENT",
      "joinedAt": "2024-01-10T09:00:00",
      "isActive": true
    }
  },
  {
    "revisionNumber": 5,
    "timestamp": "2024-02-15T14:30:00",
    "username": "instructor@example.com",
    "ipAddress": "192.168.1.50",
    "revisionType": "UPDATE",
    "entityData": {
      "id": 1,
      "role": "LEADER",  // ← Role changed from STUDENT to LEADER
      "joinedAt": "2024-01-10T09:00:00",
      "isActive": true
    }
  }
]
```

### Attachment History

```bash
# Get all changes to an attachment
GET /api/audit/attachments/{attachmentId}/history
Authorization: Bearer TOKEN

# Example Response
[
  {
    "revisionNumber": 1,
    "timestamp": "2024-01-15T10:30:00",
    "username": "student@example.com",
    "ipAddress": "192.168.1.100",
    "revisionType": "INSERT",
    "entityData": {
      "id": 1,
      "fileName": "project_proposal.pdf",
      "filePath": "/uploads/2024/01/project_proposal.pdf",
      "fileType": "application/pdf",
      "fileSize": 1024000,
      "uploadedBy": "student@example.com"
    }
  },
  {
    "revisionNumber": 3,
    "timestamp": "2024-01-20T16:45:00",
    "username": "student@example.com",
    "ipAddress": "192.168.1.100",
    "revisionType": "DELETE",
    "entityData": {
      "id": 1,
      "fileName": "project_proposal.pdf",
      "filePath": "/uploads/2024/01/project_proposal.pdf"
    }
  }
]
```

## 📊 What Gets Tracked

### ProjectMember Entity

| Field | Tracked | Purpose |
|-------|---------|---------|
| `id` | ✅ Yes | Member identifier |
| `project` | ⚠️ Reference only | Which project (NOT_AUDITED) |
| `user` | ⚠️ Reference only | Which user (NOT_AUDITED) |
| `role` | ✅ Yes | Track role changes (STUDENT → LEADER) |
| `joinedAt` | ✅ Yes | When member joined |
| `isActive` | ✅ Yes | Track activation/deactivation |

**Note:** `project` and `user` relationships use `RelationTargetAuditMode.NOT_AUDITED` because those entities have their own audit trails.

### Attachment Entity

| Field | Tracked | Purpose |
|-------|---------|---------|
| `id` | ✅ Yes | Attachment identifier |
| `fileName` | ✅ Yes | Original filename |
| `filePath` | ✅ Yes | Server file location |
| `fileType` | ✅ Yes | MIME type |
| `fileSize` | ✅ Yes | File size in bytes |
| `url` | ✅ Yes | Download URL |
| `project` | ⚠️ Reference only | Parent project (NOT_AUDITED) |
| `milestone` | ⚠️ Reference only | Parent milestone (NOT_AUDITED) |
| `task` | ⚠️ Reference only | Parent task (NOT_AUDITED) |
| `report` | ⚠️ Reference only | Parent report (NOT_AUDITED) |
| `comment` | ⚠️ Reference only | Parent comment (NOT_AUDITED) |
| `uploadedBy` | ⚠️ Reference only | Who uploaded (NOT_AUDITED) |

**Note:** All relationships use `RelationTargetAuditMode.NOT_AUDITED` to avoid circular audit dependencies.

## 💡 Example Scenarios

### Scenario 1: Investigate Role Change

**Question:** "Who promoted John to team leader?"

```bash
# 1. Get project member ID for John
GET /api/project-members?userId=john@example.com&projectId=1

# 2. Get audit history
GET /api/audit/project-members/15/history

# 3. Look for UPDATE revision where role changed from STUDENT to LEADER
# Result shows:
# - Revision 5
# - Changed by: instructor@example.com
# - Date: 2024-02-15T14:30:00
```

### Scenario 2: Track File Upload

**Question:** "Who uploaded the final report and when?"

```bash
# 1. Get attachment by filename
GET /api/attachments?fileName=final_report.pdf

# 2. Get audit history
GET /api/audit/attachments/42/history

# 3. Look for INSERT revision
# Result shows:
# - Uploaded by: student@example.com
# - Date: 2024-03-20T10:15:00
# - File size: 2048000 bytes
# - IP: 192.168.1.120
```

### Scenario 3: Investigate File Deletion

**Question:** "Who deleted the project documentation?"

```bash
# Get audit history (even for deleted files)
GET /api/audit/attachments/30/history

# Look for DELETE revision
# Result shows:
# - Deleted by: student@example.com
# - Date: 2024-02-10T16:30:00
# - Original file: project_docs.docx
# - File path: /uploads/2024/02/project_docs.docx
```

## 🔍 Audit Details

### Revision Types

- **INSERT** - Member added to project / File uploaded
- **UPDATE** - Role changed / File metadata updated
- **DELETE** - Member removed / File deleted

### Captured Information

Every change captures:
- ✅ **Who**: Username from Spring Security
- ✅ **When**: Timestamp (millisecond precision)
- ✅ **Where**: IP address (with proxy support)
- ✅ **What**: Entity state before/after change
- ✅ **How**: Revision type (INSERT/UPDATE/DELETE)

## 🧪 Testing Guide

### Test ProjectMember Audit

```bash
# 1. Add member to project
POST /api/project-members
{
  "projectId": 1,
  "userId": "student@example.com",
  "role": "STUDENT"
}

# 2. Check audit history
GET /api/audit/project-members/1/history
# Should see 1 INSERT revision

# 3. Promote to leader
PUT /api/project-members/1
{
  "role": "LEADER"
}

# 4. Check audit history again
GET /api/audit/project-members/1/history
# Should see 2 revisions: INSERT + UPDATE

# 5. Deactivate member
PUT /api/project-members/1
{
  "isActive": false
}

# 6. Check audit history
GET /api/audit/project-members/1/history
# Should see 3 revisions
```

### Test Attachment Audit

```bash
# 1. Upload file
POST /api/attachments
Content-Type: multipart/form-data
file: project_report.pdf
projectId: 1

# 2. Check audit history
GET /api/audit/attachments/1/history
# Should see 1 INSERT revision with upload details

# 3. Delete file
DELETE /api/attachments/1

# 4. Check audit history (still accessible!)
GET /api/audit/attachments/1/history
# Should see 2 revisions: INSERT + DELETE
```

## 📈 Database Schema

### project_members_AUD Table

```sql
CREATE TABLE project_members_AUD (
    id BIGINT NOT NULL,
    REV INTEGER NOT NULL,
    REVTYPE TINYINT,
    project_id BIGINT,
    user_id BIGINT,
    role VARCHAR(50),
    joined_at DATETIME,
    is_active BOOLEAN,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO(REV)
);
```

### attachments_AUD Table

```sql
CREATE TABLE attachments_AUD (
    id BIGINT NOT NULL,
    REV INTEGER NOT NULL,
    REVTYPE TINYINT,
    file_name VARCHAR(255),
    file_path VARCHAR(512),
    file_type VARCHAR(100),
    file_size BIGINT,
    url VARCHAR(512),
    project_id BIGINT,
    milestone_id BIGINT,
    task_id BIGINT,
    report_id BIGINT,
    comment_id BIGINT,
    uploaded_by_id BIGINT,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO(REV)
);
```

## 🎓 Implementation Details

### Entity Annotations

#### ProjectMember.java
```java
@Entity
@Table(name = "project_members")
@Audited
public class ProjectMember extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EUserRole role;  // ← This is tracked!
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // ← This is tracked!
}
```

#### Attachment.java
```java
@Entity
@Table(name = "attachments")
@Audited
public class Attachment extends BaseEntity {
    
    @Column(name = "file_name", nullable = false)
    private String fileName;  // ← Tracked
    
    @Column(name = "file_path", nullable = false)
    private String filePath;  // ← Tracked
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User uploadedBy;  // ← Reference only
}
```

## 🚀 Benefits

### For ProjectMember Tracking

✅ **Accountability**: Know who changed member roles
✅ **Security**: Track member additions/removals
✅ **Compliance**: Audit trail for access control
✅ **Team Management**: History of team composition

### For Attachment Tracking

✅ **File Recovery**: Know what files existed before deletion
✅ **Security**: Track who uploaded/deleted sensitive files
✅ **Compliance**: Audit trail for document management
✅ **Debugging**: Investigate missing file issues

## 📝 Summary

### What Was Added

- ✅ `@Audited` annotation to `ProjectMember` entity
- ✅ `@Audited` annotation to `Attachment` entity
- ✅ All relationships configured with `NOT_AUDITED` mode
- ✅ `getProjectMemberHistory()` method in `AuditService`
- ✅ `getAttachmentHistory()` method in `AuditService`
- ✅ `GET /api/audit/project-members/{id}/history` endpoint
- ✅ `GET /api/audit/attachments/{id}/history` endpoint
- ✅ Database tables `project_members_AUD` and `attachments_AUD` (auto-created)

### Total Audit Coverage

Now tracking **7 entities**:
1. Project
2. Task
3. Comment
4. Report
5. Milestone
6. **ProjectMember** (new!)
7. **Attachment** (new!)

### Total REST Endpoints

**11 audit endpoints** available:
- Projects (3 endpoints)
- Tasks (1 endpoint)
- Comments (1 endpoint)
- Reports (1 endpoint)
- Milestones (1 endpoint)
- **ProjectMembers (1 endpoint)** ← New!
- **Attachments (1 endpoint)** ← New!
- Admin queries (2 endpoints)

---

## 🎉 Ready to Use!

Start the application and the audit tables will be created automatically. All ProjectMember and Attachment changes are now tracked with full audit trail! 🚀

**Next Steps:**
1. Start application: `./mvnw spring-boot:run`
2. Check database for `project_members_AUD` and `attachments_AUD` tables
3. Add/modify project members and attachments
4. Query audit history via REST APIs
5. Verify username and timestamp capture
