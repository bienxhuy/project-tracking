# Spring Data Envers - Audit History Implementation

## 📚 Overview

This project uses **Hibernate Envers** to automatically track all changes to entities. Envers provides a complete audit trail showing:
- **Who** made the change (username)
- **When** the change occurred (timestamp)
- **What** was changed (old vs new values)
- **From where** (IP address)

## 🎯 Audited Entities

The following entities are being tracked:

### 1. **Project** (`projects` table)
- Status changes (PENDING → IN_PROGRESS → COMPLETED)
- Title/description edits
- Grade changes
- Lock status changes
- Audit table: `projects_AUD`

### 2. **Task** (`tasks` table)
- Task completion tracking
- Status changes (TODO → IN_PROGRESS → DONE)
- Assignment changes (who was assigned)
- Audit table: `tasks_AUD`

### 3. **Comment** (`comments` table)
- Feedback changes
- Original feedback vs edited feedback
- Comment deletions
- Audit table: `comments_AUD`

### 4. **Report** (`reports` table)
- Grade changes (8.5 → 9.0)
- Report content edits
- Status changes
- Audit table: `reports_AUD`

## 🗄️ Database Schema

Envers automatically creates audit tables when the application starts:

### Audit Tables Created

1. **`REVINFO`** - Stores revision metadata
```sql
CREATE TABLE REVINFO (
    REV INT PRIMARY KEY AUTO_INCREMENT,
    REVTSTMP BIGINT NOT NULL,
    username VARCHAR(255),
    ipAddress VARCHAR(255),
    action VARCHAR(255)
);
```

2. **`projects_AUD`** - Project audit history
```sql
CREATE TABLE projects_AUD (
    id BIGINT NOT NULL,
    REV INT NOT NULL,
    REVTYPE TINYINT,
    title VARCHAR(255),
    status VARCHAR(50),
    completion_percentage FLOAT,
    -- all other project fields...
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO(REV)
);
```

3. **`tasks_AUD`** - Task audit history
4. **`comments_AUD`** - Comment audit history
5. **`reports_AUD`** - Report audit history

### Revision Types
- `REVTYPE = 0` → INSERT (entity created)
- `REVTYPE = 1` → UPDATE (entity modified)
- `REVTYPE = 2` → DELETE (entity deleted)

## 🚀 How It Works

### Automatic Tracking

Whenever you save/update an entity:

```java
// Update project status
project.setStatus(EProjectStatus.IN_PROGRESS);
projectRepository.save(project);

// ✅ Envers automatically:
// 1. Creates new record in REVINFO (REV=5, timestamp, username, IP)
// 2. Creates new record in projects_AUD (id=1, REV=5, REVTYPE=1, status=IN_PROGRESS)
```

### Custom Revision Listener

Our `CustomRevisionListener` captures additional context:

```java
@Override
public void newRevision(Object revisionEntity) {
    CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;
    
    // Capture username from Spring Security
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    rev.setUsername(auth.getName()); // e.g., "john@example.com"
    
    // Capture IP address from request
    HttpServletRequest request = getCurrentRequest();
    rev.setIpAddress(getClientIp(request)); // e.g., "192.168.1.100"
}
```

## 📊 API Endpoints

### Project History

#### Get all project revisions
```http
GET /api/audit/projects/{projectId}/history
Authorization: Bearer <token>

Response:
[
  {
    "revisionNumber": 1,
    "timestamp": "2024-01-01T10:00:00",
    "username": "john@example.com",
    "ipAddress": "192.168.1.100",
    "revisionType": "INSERT",
    "entityData": { "id": 1, "title": "My Project", "status": "PENDING", ... }
  },
  {
    "revisionNumber": 2,
    "timestamp": "2024-01-02T14:30:00",
    "username": "john@example.com",
    "ipAddress": "192.168.1.100",
    "revisionType": "UPDATE",
    "entityData": { "id": 1, "title": "My Project", "status": "IN_PROGRESS", ... }
  }
]
```

#### Get project at specific revision
```http
GET /api/audit/projects/{projectId}/revisions/2
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "title": "My Project",
  "status": "IN_PROGRESS",  // Status at revision 2
  ...
}
```

#### Get project at specific date
```http
GET /api/audit/projects/{projectId}/at-date?date=2024-01-03T10:00:00
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "status": "IN_PROGRESS",  // What was status on Jan 3?
  ...
}
```

### Task History

#### Track task completion
```http
GET /api/audit/tasks/{taskId}/history
Authorization: Bearer <token>

Response:
[
  {
    "revisionNumber": 10,
    "timestamp": "2024-01-05T16:00:00",
    "username": "student1@example.com",
    "revisionType": "UPDATE",
    "entityData": { "id": 5, "status": "DONE", ... }
  }
]
```

### Comment/Feedback History

#### Track feedback changes
```http
GET /api/audit/comments/{commentId}/history
Authorization: Bearer <token>

Response:
[
  {
    "revisionNumber": 4,
    "timestamp": "2024-01-06T09:00:00",
    "username": "teacher1@example.com",
    "revisionType": "INSERT",
    "entityData": { "id": 10, "content": "Good work, but needs improvement", ... }
  },
  {
    "revisionNumber": 5,
    "timestamp": "2024-01-07T10:00:00",
    "username": "teacher1@example.com",
    "revisionType": "UPDATE",
    "entityData": { "id": 10, "content": "Excellent! Well done", ... }
  }
]
```

### Report/Grade History

#### Track grade changes
```http
GET /api/audit/reports/{reportId}/history
Authorization: Bearer <token>

Response:
[
  {
    "revisionNumber": 8,
    "timestamp": "2024-01-06T09:00:00",
    "username": "teacher1@example.com",
    "revisionType": "UPDATE",
    "entityData": { "id": 3, "grade": 8.5, ... }
  },
  {
    "revisionNumber": 9,
    "timestamp": "2024-01-07T10:00:00",
    "username": "teacher1@example.com",
    "revisionType": "UPDATE",
    "entityData": { "id": 3, "grade": 9.0, ... }  // Changed grade!
  }
]
```

### Admin/Instructor Only Endpoints

#### Get all changes by a user
```http
GET /api/audit/users/john@example.com/changes
Authorization: Bearer <admin-token>
```

#### Get changes in date range
```http
GET /api/audit/changes?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
Authorization: Bearer <admin-token>
```

## 🔍 Use Cases

### Use Case 1: Track Project Status Changes

**Question:** "Who changed this project from PENDING to IN_PROGRESS?"

```java
List<AuditRevisionDTO> history = auditService.getProjectHistory(projectId);

// Find status change
for (AuditRevisionDTO rev : history) {
    Project project = (Project) rev.getEntityData();
    if (project.getStatus() == EProjectStatus.IN_PROGRESS) {
        System.out.println(rev.getUsername() + " changed status to IN_PROGRESS at " + rev.getTimestamp());
        // Output: "john@example.com changed status to IN_PROGRESS at 2024-01-02 14:30:15"
    }
}
```

### Use Case 2: Track Feedback Changes

**Question:** "What was the original feedback before teacher edited it?"

```java
List<AuditRevisionDTO> history = auditService.getCommentHistory(commentId);

AuditRevisionDTO original = history.get(0); // First revision (INSERT)
Comment originalComment = (Comment) original.getEntityData();
System.out.println("Original feedback: " + originalComment.getContent());

AuditRevisionDTO latest = history.get(history.size() - 1); // Latest revision
Comment latestComment = (Comment) latest.getEntityData();
System.out.println("Latest feedback: " + latestComment.getContent());
```

### Use Case 3: Track Grade Changes

**Question:** "How many times did teacher change the grade?"

```java
List<AuditRevisionDTO> history = auditService.getReportHistory(reportId);

int gradeChanges = 0;
Double previousGrade = null;

for (AuditRevisionDTO rev : history) {
    Report report = (Report) rev.getEntityData();
    if (previousGrade != null && !previousGrade.equals(report.getGrade())) {
        gradeChanges++;
        System.out.println(rev.getUsername() + " changed grade from " + 
            previousGrade + " to " + report.getGrade() + " at " + rev.getTimestamp());
    }
    previousGrade = report.getGrade();
}

System.out.println("Total grade changes: " + gradeChanges);
```

### Use Case 4: Compliance Audit

**Question:** "Show all changes made in January 2024 for accreditation report"

```java
LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

List<AuditRevisionDTO> changes = auditService.getChangesBetweenDates(start, end);

// Generate report
for (AuditRevisionDTO change : changes) {
    System.out.println(change.getTimestamp() + " - " + 
        change.getUsername() + " - " + 
        change.getRevisionType() + " - " +
        change.getEntityData().getClass().getSimpleName());
}
```

## ⚙️ Configuration

### Enable Envers (Already Done)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-envers</artifactId>
</dependency>
```

### Annotate Entities

```java
@Entity
@Audited  // ← Just add this annotation!
public class Project extends ProgressEntity {
    // ...
}
```

### Exclude Collections (Optional)

```java
@OneToMany(mappedBy = "project")
@NotAudited  // Don't track this collection
private List<Task> tasks;
```

### Custom Revision Entity

```java
@Entity
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity extends DefaultRevisionEntity {
    private String username;
    private String ipAddress;
    private String action;
}
```

## 📈 Performance Considerations

### Storage Overhead
- **Each UPDATE = 1 new audit row**
- Example: Project updated 100 times = 100 rows in `projects_AUD`
- **Solution:** Archive old audit data periodically

### Query Performance
- Audit queries JOIN multiple tables (entity + REVINFO)
- **Solution:** Index on `(id, REV)`, cache recent revisions

### Write Performance
- Each save writes to 2 tables (main + audit)
- **Impact:** ~10-20% slower writes (acceptable for most cases)
- **Solution:** Async audit if needed (advanced)

## 🔒 Security

### Access Control
- Regular users: Can only view history of their own entities
- Instructors: Can view history of their projects/students
- Admins: Can view all audit history

```java
@GetMapping("/projects/{projectId}/history")
@PreAuthorize("@auditSecurity.canViewProjectHistory(#projectId)")
public ResponseEntity<List<AuditRevisionDTO>> getProjectHistory(@PathVariable Long projectId) {
    // ...
}
```

### Audit Trail Integrity
- Audit tables should be **read-only** in application
- Only Envers can write to audit tables
- Consider database-level permissions to prevent tampering

## 🧪 Testing

### Test Audit Tracking

```java
@Test
void testProjectAuditTracking() {
    // Create project
    Project project = new Project();
    project.setTitle("Test Project");
    project.setStatus(EProjectStatus.PENDING);
    project = projectRepository.save(project);
    
    // Update status
    project.setStatus(EProjectStatus.IN_PROGRESS);
    projectRepository.save(project);
    
    // Verify audit history
    List<AuditRevisionDTO> history = auditService.getProjectHistory(project.getId());
    assertEquals(2, history.size());  // INSERT + UPDATE
    
    // Check first revision (INSERT)
    assertEquals("INSERT", history.get(0).getRevisionType());
    Project rev1 = (Project) history.get(0).getEntityData();
    assertEquals(EProjectStatus.PENDING, rev1.getStatus());
    
    // Check second revision (UPDATE)
    assertEquals("UPDATE", history.get(1).getRevisionType());
    Project rev2 = (Project) history.get(1).getEntityData();
    assertEquals(EProjectStatus.IN_PROGRESS, rev2.getStatus());
}
```

## 📝 Frontend Integration

### Display History Timeline

```typescript
// Fetch project history
const response = await fetch(`/api/audit/projects/${projectId}/history`);
const history = await response.json();

// Display in UI
<Timeline>
  {history.map(rev => (
    <TimelineItem key={rev.revisionNumber}>
      <TimelineDate>{rev.timestamp}</TimelineDate>
      <TimelineContent>
        {rev.username} {rev.revisionType} - Status: {rev.entityData.status}
      </TimelineContent>
    </TimelineItem>
  ))}
</Timeline>
```

### Compare Revisions

```typescript
// Get project at two different revisions
const rev1 = await fetch(`/api/audit/projects/${projectId}/revisions/1`);
const rev2 = await fetch(`/api/audit/projects/${projectId}/revisions/2`);

// Show diff
<DiffViewer
  oldValue={rev1.status}
  newValue={rev2.status}
  label="Status"
/>
```

## 🎓 Summary

**Envers provides:**
- ✅ Automatic audit tracking
- ✅ Complete change history
- ✅ Who/When/What information
- ✅ Point-in-time queries
- ✅ Compliance support
- ✅ Rollback capability (view old state)

**Perfect for:**
- Educational systems (track grades, feedback)
- Compliance requirements
- Accountability & transparency
- Dispute resolution
- Data forensics

**Next Steps:**
1. Start application → Audit tables auto-created
2. Make some changes to projects/tasks
3. Call `/api/audit/projects/{id}/history`
4. See the magic! 🎉

---

**Note:** Remember to configure security properly to prevent unauthorized access to audit history!
