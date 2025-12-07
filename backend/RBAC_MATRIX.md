# Role-Based Access Control Matrix

## Service Authorization Table

| Service | Method | ADMIN | INSTRUCTOR | STUDENT | FR Reference | Description |
|---------|--------|-------|------------|---------|--------------|-------------|
| **AuthService** | `login()` | ✅ | ✅ | ✅ | FR-001 | User login with email/password |
| **AuthService** | `logout()` | ✅ | ✅ | ✅ | FR-003 | User logout |
| **AuthService** | `refreshToken()` | ✅ | ✅ | ✅ | FR-001 | Refresh JWT token |
| **UserService** | `createUser()` | ✅ | ❌ | ❌ | FR-004 | Create individual user account |
| **UserService** | `bulkCreateUsers()` | ✅ | ❌ | ❌ | FR-005 | Bulk create accounts via CSV |
| **UserService** | `updateUser()` | ✅ | ❌ | ❌ | FR-006 | Update user information |
| **UserService** | `deleteUser()` | ✅ | ❌ | ❌ | FR-006 | Delete/deactivate user |
| **UserService** | `getAllUsers()` | ✅ | ❌ | ❌ | FR-006 | List all users |
| **UserService** | `getUserById()` | ✅ | ✅* | ✅* | - | View user profile (*own profile or project members) |
| **ProjectService** | `createProject()` | ❌ | ✅ | ❌ | FR-007 | Create new project |
| **ProjectService** | `updateProject()` | ❌ | ✅* | ❌ | FR-007 | Update project (*own project only) |
| **ProjectService** | `deleteProject()` | ❌ | ✅* | ❌ | FR-007 | Delete project (*own project only) |
| **ProjectService** | `addStudentsToProject()` | ❌ | ✅* | ❌ | FR-008 | Add students to project (*own project) |
| **ProjectService** | `removeStudentFromProject()` | ❌ | ✅* | ❌ | FR-009 | Remove student from project (*own project) |
| **ProjectService** | `getMyProjects()` | ❌ | ✅ | ✅ | FR-010 | View assigned projects |
| **ProjectService** | `getProjectById()` | ❌ | ✅* | ✅* | FR-010 | View project details (*if member) |
| **ProjectService** | `searchProjects()` | ❌ | ✅ | ✅ | FR-023 | Search projects |
| **ProjectService** | `updateProjectContent()` | ❌ | ❌ | ✅* | FR-011 | Define objectives & contents (*if member & not locked) |
| **ProjectService** | `lockProjectContent()` | ❌ | ✅* | ❌ | FR-021 | Lock objectives & contents (*own project) |
| **ProjectService** | `unlockProjectContent()` | ❌ | ✅* | ❌ | FR-021 | Unlock objectives & contents (*own project) |
| **ProjectService** | `lockProject()` | ❌ | ✅* | ❌ | FR-021 | Lock entire project (*own project) |
| **ProjectService** | `unlockProject()` | ❌ | ✅* | ❌ | FR-021 | Unlock entire project (*own project) |
| **ProjectService** | `exportProjectReport()` | ❌ | ✅* | ❌ | FR-022 | Export project report PDF (*own project) |
| **MilestoneService** | `createMilestone()` | ❌ | ❌ | ✅* | FR-012 | Create milestone (*if project member & not locked) |
| **MilestoneService** | `updateMilestone()` | ❌ | ❌ | ✅* | FR-012 | Update milestone (*if member & not locked) |
| **MilestoneService** | `deleteMilestone()` | ❌ | ❌ | ✅* | FR-012 | Delete milestone (*if member & not locked) |
| **MilestoneService** | `getMilestoneById()` | ❌ | ✅* | ✅* | FR-012 | View milestone details (*if project member) |
| **MilestoneService** | `lockMilestone()` | ❌ | ✅* | ❌ | FR-021 | Lock milestone (*instructor of project) |
| **MilestoneService** | `unlockMilestone()` | ❌ | ✅* | ❌ | FR-021 | Unlock milestone (*instructor of project) |
| **TaskService** | `createTask()` | ❌ | ❌ | ✅* | FR-013 | Create task under milestone (*if member & not locked) |
| **TaskService** | `updateTask()` | ❌ | ❌ | ✅* | FR-013 | Update task (*if member & not locked) |
| **TaskService** | `deleteTask()` | ❌ | ❌ | ✅* | FR-013 | Delete task (*if member & not locked) |
| **TaskService** | `assignTaskToMembers()` | ❌ | ❌ | ✅* | FR-014 | Assign task to team members (*if member) |
| **TaskService** | `markTaskComplete()` | ❌ | ❌ | ✅* | FR-016 | Mark task as complete (*if assignee) |
| **TaskService** | `getTaskById()` | ❌ | ✅* | ✅* | FR-013 | View task details (*if project member) |
| **TaskService** | `lockTask()` | ❌ | ✅* | ❌ | FR-021 | Lock task (*instructor of project) |
| **TaskService** | `unlockTask()` | ❌ | ✅* | ❌ | FR-021 | Unlock task (*instructor of project) |
| **ReportService** | `createReport()` | ❌ | ❌ | ✅* | FR-015 | Submit progress report (*if task assignee & not locked) |
| **ReportService** | `updateReport()` | ❌ | ❌ | ✅* | FR-015 | Update report (*if author & not locked) |
| **ReportService** | `deleteReport()` | ❌ | ❌ | ✅* | FR-015 | Delete report (*if author & not locked) |
| **ReportService** | `getReportById()` | ❌ | ✅* | ✅* | FR-015 | View report (*if project member) |
| **ReportService** | `lockReport()` | ❌ | ✅* | ❌ | FR-021 | Lock report (*instructor of project) |
| **ReportService** | `unlockReport()` | ❌ | ✅* | ❌ | FR-021 | Unlock report (*instructor of project) |
| **CommentService** | `addComment()` | ❌ | ✅* | ✅* | FR-020 | Add comment on report (*if project member) |
| **CommentService** | `updateComment()` | ❌ | ✅* | ✅* | FR-020 | Edit comment within 5 minutes (*if author) |
| **CommentService** | `getCommentsByReport()` | ❌ | ✅* | ✅* | FR-020 | View comments (*if project member) |
| **NotificationService** | `getMyNotifications()` | ✅ | ✅ | ✅ | FR-024 | Get user's notifications |
| **NotificationService** | `markAsRead()` | ✅ | ✅ | ✅ | FR-024 | Mark notification as read |
| **NotificationService** | `countUnread()` | ✅ | ✅ | ✅ | FR-024 | Count unread notifications |
| **DashboardService** | `getGlobalStats()` | ✅ | ❌ | ❌ | - | View global statistics |
| **DashboardService** | `getUserStats()` | ❌ | ✅* | ✅* | - | View personal statistics (*own stats) |

## Legend:
- ✅ : Full access
- ❌ : No access
- ✅* : Conditional access (with business logic validation)

## Additional Business Rules:

### 1. Project Membership Validation
- Students can only access projects they are members of
- Instructors can only modify their own projects
- All modifications check if entity is locked (FR-021)

### 2. Lock Hierarchy (FR-021)
```
Project (locked) → All Milestones locked
  ↓
Milestone (locked) → All Tasks locked
  ↓
Task (locked) → All Reports locked
```

### 3. Notification Triggers (FR-024)
- Student added to project → Student receives notification
- Student removed from project → Student receives notification  
- Task assigned → Assignee receives notification
- Comment added → All project members notified
- Milestone deadline approaching (3 days) → All members notified
- Task marked complete → Team members & instructor notified
- Project/Milestone/Task/Report locked → Students notified

### 4. Session & Security (FR-001, FR-003)
- Session expires after 30 minutes inactivity
- Account locked after 5 failed login attempts
- All endpoints (except public) require JWT authentication

### 5. File Upload Limits (FR-015)
- Report attachments: max 50MB
- Supported formats: PDF, DOC, DOCX, ZIP

### 6. Search Behavior (FR-023)
- Case-insensitive search
- Live search (results update as typing)
- Search by: project name, keywords

## Implementation Notes:

### @PreAuthorize Annotations:
```java
// Admin only
@PreAuthorize("hasRole('ADMIN')")

// Instructor only  
@PreAuthorize("hasRole('INSTRUCTOR')")

// Student only
@PreAuthorize("hasRole('STUDENT')")

// Student or Instructor
@PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")

// All authenticated users
@PreAuthorize("isAuthenticated()")

// Instructor of specific project
@PreAuthorize("hasRole('INSTRUCTOR') and @projectSecurityService.isProjectInstructor(#projectId)")

// Project member
@PreAuthorize("@projectSecurityService.isProjectMember(#projectId)")

// Task assignee
@PreAuthorize("@taskSecurityService.isTaskAssignee(#taskId)")
```

### Custom Security Services Needed:
1. `ProjectSecurityService` - Check project membership/ownership
2. `TaskSecurityService` - Check task assignment
3. `LockValidationService` - Check if entity is locked
