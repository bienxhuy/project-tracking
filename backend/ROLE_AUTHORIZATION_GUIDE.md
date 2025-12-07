# Role-Based Authorization Guide

## Áp dụng theo Functional Requirements

### FR-004, FR-005, FR-006: User Account Management (ADMIN only)
```java
// UserServiceImpl
@PreAuthorize("hasRole('ADMIN')")
public UserRes createUser(UserReq userReq)

@PreAuthorize("hasRole('ADMIN')")
public List<UserRes> bulkCreateUsers(MultipartFile file)

@PreAuthorize("hasRole('ADMIN')")
public UserRes updateUser(Long id, UserUpdateReq userUpdateReq)

@PreAuthorize("hasRole('ADMIN')")  
public List<UserRes> getAllUsers()
```

### FR-007: Project Creation (INSTRUCTOR only)
```java
// ProjectServiceImpl
@PreAuthorize("hasRole('INSTRUCTOR')")
public ProjectRes createProject(ProjectReq projectReq)
```

### FR-008, FR-009: Add/Remove Students (INSTRUCTOR only)
```java
// ProjectServiceImpl
@PreAuthorize("hasRole('INSTRUCTOR')")
public ProjectRes addStudentsToProject(Long projectId, List<Long> studentIds)

@PreAuthorize("hasRole('INSTRUCTOR')")
public ProjectRes removeStudentFromProject(Long projectId, Long studentId)
```

### FR-010: View Assigned Projects (STUDENT, INSTRUCTOR)
```java
// ProjectServiceImpl  
@PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
public List<ProjectRes> getMyProjects()

@PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
public ProjectRes getProjectById(Long id)
```

### FR-011: Define Objectives (STUDENT only)
```java
// ProjectServiceImpl
@PreAuthorize("hasRole('STUDENT')")
public ProjectRes updateProjectObjectives(Long projectId, String objectives, String contents)
```

### FR-012: Create Milestones (STUDENT only)
```java
// MilestoneServiceImpl
@PreAuthorize("hasRole('STUDENT')")
public MilestoneRes createMilestone(MilestoneReq milestoneReq)
```

### FR-013, FR-014: Create & Assign Tasks (STUDENT only)
```java
// TaskServiceImpl
@PreAuthorize("hasRole('STUDENT')")
public TaskRes createTask(TaskReq taskReq)

@PreAuthorize("hasRole('STUDENT')")
public TaskRes assignTaskToMembers(Long taskId, List<Long> memberIds)
```

### FR-015: Submit Reports (STUDENT only)
```java
// ReportServiceImpl
@PreAuthorize("hasRole('STUDENT')")
public ReportRes createReport(ReportReq reportReq)
```

### FR-016: Mark Task Complete (STUDENT only)
```java
// TaskServiceImpl
@PreAuthorize("hasRole('STUDENT')")
public TaskRes markTaskComplete(Long taskId)
```

### FR-020: Comments on Reports (STUDENT, INSTRUCTOR)
```java
// CommentServiceImpl
@PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
public CommentRes addComment(CommentReq commentReq)
```

### FR-021: Lock Work (INSTRUCTOR only)
```java
// ProjectServiceImpl
@PreAuthorize("hasRole('INSTRUCTOR')")
public void lockObjectives(Long projectId)

// MilestoneServiceImpl
@PreAuthorize("hasRole('INSTRUCTOR')")
public void lockMilestone(Long milestoneId)

// TaskServiceImpl
@PreAuthorize("hasRole('INSTRUCTOR')")
public void lockTask(Long taskId)

// ReportServiceImpl
@PreAuthorize("hasRole('INSTRUCTOR')")
public void lockReport(Long reportId)
```

### FR-022: Export Reports (INSTRUCTOR only)
```java
// ReportServiceImpl
@PreAuthorize("hasRole('INSTRUCTOR')")
public byte[] exportProjectReport(Long projectId, String format)
```

### FR-023: Search Projects (STUDENT, INSTRUCTOR)
```java
// ProjectServiceImpl
@PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
public List<ProjectRes> searchProjects(String keyword)
```

## Implementation Status

✅ **Completed:**
- UserServiceImpl.createUser() - ADMIN
- UserServiceImpl.getAllUsers() - ADMIN  
- ProjectServiceImpl.createProject() - INSTRUCTOR

⏳ **TODO:**
- Add remaining @PreAuthorize annotations to all service methods
- Add method-level security checks for project membership
- Add validation for locked items (FR-021)
