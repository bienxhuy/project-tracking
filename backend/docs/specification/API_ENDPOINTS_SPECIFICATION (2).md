# Frontend API Endpoints Specification

**Project:** Project Tracking System  
**Scope:** All non-admin routes (Student & Instructor features)

This document specifies all backend API endpoints required by the frontend application, excluding admin-specific functionality.

---

## Table of Contents

1. [Authentication & Authorization](#1-authentication--authorization)
2. [User Management](#2-user-management)
3. [Project Management](#3-project-management)
4. [Milestone Management](#4-milestone-management)
5. [Task Management](#5-task-management)
6. [Report Management](#6-report-management)
7. [Comment Management](#7-comment-management)
8. [Notification Management](#8-notification-management)
9. [Firebase Device Token Management](#9-firebase-device-token-management)
10. [Semester & Faculty Management](#10-semester--faculty-management)
11. [File/Attachment Management](#11-fileattachment-management)

---

## Common Data Structures

### ApiResponse<T>
```typescript
{
  status: number,
  message: string,
  data: T,
  errorCode?: string
}
```

### User
```typescript
{
  id: number,
  username: string,
  email: string,
  displayName: string,
  avatar?: string,
  role: "STUDENT" | "INSTRUCTOR" | "ADMIN",
  accountStatus: "ACTIVE" | "INACTIVE" | "VERIFYING",
  level: number,
  loginType: "LOCAL" | "GOOGLE",
  createdAt: string,
  updatedAt: string
}
```

### BaseUser (Minimal User Info)
```typescript
{
  id: number,
  displayName: string,
  email: string,
  role: "STUDENT" | "INSTRUCTOR" | "ADMIN"
}
```
**Note:** Initials are extracted from `displayName` on the frontend using a helper function.

---

## 1. Authentication & Authorization

### 1.1 Login
- **Method:** `POST`
- **URL:** `/api/v1/auth/login`
- **Description:** Authenticate user and receive access token
- **Request Body:**
```typescript
{
  identifier: string,  // username or email
  password: string
}
```
- **Response:** `ApiResponse<LoginResponse>`
```typescript
{
  accessToken: string,
  userId?: number
}
```

### 1.2 Register
- **Method:** `POST`
- **URL:** `/api/v1/auth/register`
- **Description:** Create new user account
- **Request Body:**
```typescript
{
  username: string,
  email: string,
  password: string,
  displayName: string
}
```
- **Response:** `ApiResponse<User>`

### 1.3 Logout
- **Method:** `GET`
- **URL:** `/api/v1/auth/logout`
- **Description:** Log out current user
- **Response:** `ApiResponse<boolean>`

### 1.4 Get Current User Account
- **Method:** `GET`
- **URL:** `/api/v1/auth/account`
- **Description:** Get current authenticated user information
- **Response:** `ApiResponse<User>`

### 1.5 Refresh Token
- **Method:** `GET`
- **URL:** `/api/v1/auth/refresh`
- **Description:** Refresh access token
- **Response:** `ApiResponse<LoginResponse>`

---

## 2. User Management

### 2.1 Get All Users (with filters)
- **Method:** `GET`
- **URL:** `/api/v1/users`
- **Description:** Get list of users with optional filters
- **Query Parameters:**
  - `search?: string` - Search by username, email, displayName
  - `role?: "STUDENT" | "INSTRUCTOR" | "ADMIN" | "ALL"`
  - `accountStatus?: "ACTIVE" | "INACTIVE" | "VERIFYING" | "ALL"`
  - `loginType?: "LOCAL" | "GOOGLE" | "ALL"`
- **Response:** `ApiResponse<User[]>`

### 2.2 Get User by ID
- **Method:** `GET`
- **URL:** `/api/v1/users/{id}`
- **Description:** Get detailed information of a specific user
- **Response:** `ApiResponse<User>`

### 2.3 Update User
- **Method:** `PUT`
- **URL:** `/api/v1/users/{id}`
- **Description:** Update user information
- **Request Body:**
```typescript
{
  username?: string,
  password?: string,
  email?: string,
  displayName?: string,
  role?: "STUDENT" | "INSTRUCTOR" | "ADMIN",
  accountStatus?: "ACTIVE" | "INACTIVE" | "VERIFYING",
  level?: number,
  avatar?: string
}
```
- **Response:** `ApiResponse<User>`

### 2.4 Update User Status
- **Method:** `PUT`
- **URL:** `/api/v1/users/{id}/status`
- **Query Parameters:**
  - `status: "ACTIVE" | "INACTIVE" | "VERIFYING"`
- **Response:** `ApiResponse<User>`

### 2.5 Delete User
- **Method:** `DELETE`
- **URL:** `/api/v1/users/{id}`
- **Response:** `ApiResponse<void>`

### 2.6 Search Students
- **Method:** `GET`
- **URL:** `/api/v1/users/students/search`
- **Description:** Search for students by name or email
- **Query Parameters:**
  - `query?: string` - Search query
- **Response:** `ApiResponse<BaseUser[]>`

### 2.7 Get All Students
- **Method:** `GET`
- **URL:** `/api/v1/users/students`
- **Description:** Get all students (for student selector)
- **Response:** `ApiResponse<BaseUser[]>`

---

## 3. Project Management

### Project Data Structure
```typescript
{
  id: number,
  title: string,
  objective: string,
  content: string,
  year: number,
  semester: number,
  batch: number,
  falculty: string,
  startDate: Date,
  endDate: Date,
  milestoneCount: number,
  memberCount: number,
  completionPercentage: number,
  status: "ACTIVE" | "COMPLETED",
  isLocked: boolean
}
```

### ProjectDetail
```typescript
{
  ...Project,
  isObjDesLocked: boolean,
  milestones: Milestone[],
  students: BaseUser[]
}
```

### 3.1 Get Project by ID
- **Method:** `GET`
- **URL:** `/api/v1/projects/{id}`
- **Description:** Get detailed project information with milestones and members
- **Response:** `ApiResponse<ProjectDetail>`

### 3.2 Create Project
- **Method:** `POST`
- **URL:** `/api/v1/projects`
- **Description:** Create a new project (Instructor only)
- **Request Body:**
```typescript
{
  title: string,
  objective: string,
  content: string,
  year: number,
  semester: number,
  batch: number,
  falculty: string,
  startDate: string,
  endDate: string,
  studentIds: number[]
}
```
- **Response:** `ApiResponse<Project>`

### 3.3 Update Project
- **Method:** `PUT`
- **URL:** `/api/v1/projects/{id}`
- **Description:** Update existing project (Instructor only)
- **Request Body:**
```typescript
{
  title: string,
  objective: string,
  content: string,
  year: number,
  semester: number,
  batch: number,
  falculty: string,
  startDate: string,
  endDate: string,
  studentIds: number[]
}
```
- **Response:** `ApiResponse<Project>`

### 3.4 Delete Project
- **Method:** `DELETE`
- **URL:** `/api/v1/projects/{id}`
- **Description:** Delete a project (Instructor only)
- **Response:** `ApiResponse<void>`

### 3.5 Update Project Objective & Content
- **Method:** `PATCH`
- **URL:** `/api/v1/projects/{id}/content`
- **Description:** Update project objective and description (Student)
- **Request Body:**
```typescript
{
  objective: string,
  content: string
}
```
- **Response:** `ApiResponse<Project>`

### 3.6 Lock Project Content
- **Method:** `PATCH`
- **URL:** `/api/v1/projects/{id}/lock`
- **Description:** Lock project objective and description (Instructor only)
- **Response:** `ApiResponse<Project>`

### 3.7 Unlock Project Content
- **Method:** `PATCH`
- **URL:** `/api/v1/projects/{id}/unlock`
- **Description:** Unlock project objective and description (Instructor only)
- **Response:** `ApiResponse<Project>`

### 3.8 Get Student's Projects
- **Method:** `GET`
- **URL:** `/api/v1/projects/my-projects`
- **Description:** Get all projects for the current student
- **Query Parameters:**
  - `year?: number` (optional - filter by year)
  - `semester?: number` (optional - filter by semester)
  - `batch?: number` (optional - filter by batch)
- **Response:** `ApiResponse<Project[]>`
- **Security Note:** Only returns projects where current user is a member

### 3.9 Get Instructor's Projects
- **Method:** `GET`
- **URL:** `/api/v1/projects/instructor-projects`
- **Description:** Get all projects created by the current instructor
- **Query Parameters:**
  - `year?: number` (optional - filter by year)
  - `semester?: number` (optional - filter by semester)
  - `batch?: number` (optional - filter by batch)
- **Response:** `ApiResponse<Project[]>`
- **Security Note:** Only returns projects created by current instructor

---

## 4. Milestone Management

### Milestone Data Structure
```typescript
{
  id: number,
  title: string,
  description: string,
  startDate: Date,
  endDate: Date,
  status: "IN_PROGRESS" | "COMPLETED",
  isLocked: boolean,
  orderNumber: number,
  completionPercentage: number,
  tasksTotal: number,
  tasksCompleted: number
}
```

### MilestoneDetail
```typescript
{
  ...Milestone,
  tasks: Task[]
}
```

### 4.1 Get Milestones by Project
- **Method:** `GET`
- **URL:** `/api/v1/milestones/project/{projectId}`
- **Description:** Get all milestones for a project, optionally including tasks
- **Query Parameters:**
  - `include?: string` - If set to `"tasks"`, includes tasks array in each milestone
- **Response:** `ApiResponse<Milestone[]>` or `ApiResponse<MilestoneDetail[]>` (when `include=tasks`)
- **Note:** Backend only returns specified fields. When `include=tasks`, response includes `tasks` array; otherwise only milestone data with `tasksTotal` and `tasksCompleted` counts.

### 4.2 Get Milestone by ID
- **Method:** `GET`
- **URL:** `/api/v1/milestones/{id}`
- **Description:** Get detailed milestone information, optionally including tasks
- **Query Parameters:**
  - `include?: string` - If set to `"tasks"`, includes tasks array in the milestone
- **Response:** `ApiResponse<Milestone>` or `ApiResponse<MilestoneDetail>` (when `include=tasks`)
- **Note:** Backend only returns specified fields. When `include=tasks`, response includes `tasks` array; otherwise only milestone data with `tasksTotal` and `tasksCompleted` counts.

### 4.3 Create Milestone
- **Method:** `POST`
- **URL:** `/api/v1/projects/{projectId}/milestones`
- **Description:** Create a new milestone in a project (Student)
- **Request Body:**
```typescript
{
  title: string,
  description: string,
  startDate: string,  // ISO date format
  endDate: string
}
```
- **Response:** `ApiResponse<Milestone>`

### 4.4 Update Milestone
- **Method:** `PUT`
- **URL:** `/api/v1/milestones/{id}`
- **Description:** Update milestone information (Student, if not locked)
- **Request Body:**
```typescript
{
  title: string,
  description: string,
  startDate: string,
  endDate: string
}
```
- **Response:** `ApiResponse<Milestone>`

### 4.5 Delete Milestone
- **Method:** `DELETE`
- **URL:** `/api/v1/milestones/{id}`
- **Description:** Delete a milestone (Student, if not locked)
- **Response:** `ApiResponse<void>`

### 4.6 Toggle Milestone Lock
- **Method:** `PATCH`
- **URL:** `/api/v1/milestones/{id}/lock`
- **Description:** Lock/unlock milestone (Instructor only)
- **Request Body:**
```typescript
{
  isLocked: boolean
}
```
- **Response:** `ApiResponse<Milestone>`

---

## 5. Task Management

### Task Data Structure
```typescript
{
  id: number,
  title: string,
  description: string,
  startDate: Date,
  endDate: Date,
  status: "IN_PROGRESS" | "COMPLETED",
  isLocked: boolean,
  assignees: Array<{ id: number, name: string, initials: string }>
}
```

### TaskDetail
```typescript
{
  ...Task,
  reports: Report[]
}
```

### 5.1 Get Tasks by Milestone
- **Method:** `GET`
- **URL:** `/api/v1/milestones/{milestoneId}/tasks`
- **Description:** Get all tasks in a milestone
- **Response:** `ApiResponse<Task[]>`

### 5.2 Get Task by ID
- **Method:** `GET`
- **URL:** `/api/v1/tasks/{id}`
- **Description:** Get detailed task information with reports
- **Response:** `ApiResponse<TaskDetail>`

### 5.3 Create Task
- **Method:** `POST`
- **URL:** `/api/v1/milestones/{milestoneId}/tasks`
- **Description:** Create a new task in a milestone (Student)
- **Request Body:**
```typescript
{
  title: string,
  description: string,
  startDate: string,
  endDate: string,
  assigneeIds: number[]
}
```
- **Response:** `ApiResponse<Task>`

### 5.4 Update Task
- **Method:** `PUT`
- **URL:** `/api/v1/tasks/{id}`
- **Description:** Update task information (Student, if not locked)
- **Request Body:**
```typescript
{
  title: string,
  description: string,
  startDate: string,
  endDate: string,
  assigneeIds: number[]
}
```
- **Response:** `ApiResponse<Task>`

### 5.5 Delete Task
- **Method:** `DELETE`
- **URL:** `/api/v1/tasks/{id}`
- **Description:** Delete a task (Student, if not locked)
- **Response:** `ApiResponse<void>`

### 5.6 Toggle Task Status
- **Method:** `PATCH`
- **URL:** `/api/v1/tasks/{id}/status`
- **Description:** Toggle task completion status (Student)
- **Request Body:**
```typescript
{
  status: "IN_PROGRESS" | "COMPLETED"
}
```
- **Response:** `ApiResponse<Task>`

### 5.7 Toggle Task Lock
- **Method:** `PATCH`
- **URL:** `/api/v1/tasks/{id}/lock`
- **Description:** Lock/unlock task (Instructor only)
- **Request Body:**
```typescript
{
  isLocked: boolean
}
```
- **Response:** `ApiResponse<Task>`

---

## 6. Report Management

### Report Data Structure
```typescript
{
  id: number,
  title: string,
  content: string,
  reporter: BaseUser,  // User who created the report
  attachments: Attachment[],
  status: "SUBMITTED" | "LOCKED"
}
```

### ReportDetail
```typescript
{
  ...Report,
  comments: Comment[]
}
```

### 6.1 Get Reports by Task
- **Method:** `GET`
- **URL:** `/api/v1/tasks/{taskId}/reports`
- **Description:** Get all progress reports for a task
- **Response:** `ApiResponse<Report[]>`
- **Security Note:** Only returns reports if user has access to the parent task/project
- **Note:** Each report includes reporter information (BaseUser)

### 6.2 Get Report by ID
- **Method:** `GET`
- **URL:** `/api/v1/reports/{id}`
- **Description:** Get detailed report with comments
- **Response:** `ApiResponse<ReportDetail>`
- **Note:** Includes full reporter information and all comments

### 6.3 Create Report
- **Method:** `POST`
- **URL:** `/api/v1/tasks/{taskId}/reports`
- **Description:** Create a new progress report with file uploads
- **Content-Type:** `multipart/form-data`
- **Request Body (Form Data):**
  - `title: string`
  - `content: string`
  - `files: File[]` (multiple files)
- **Response:** `ApiResponse<Report>`
- **Authorization:** Uses JWT token to identify the reporter (current user)
- **Note:** Reporter is automatically set from the authenticated user's token, not from request body
- **Permission:** Only report creator can edit the report later

### 6.4 Update Report
- **Method:** `PUT`
- **URL:** `/api/v1/reports/{id}`
- **Description:** Update existing report (if not locked and user is the reporter)
- **Content-Type:** `multipart/form-data`
- **Request Body (Form Data):**
  - `title: string`
  - `content: string`
  - `files: File[]` (new files to add)
  - `existingAttachmentIds: number[]` (keep these attachments)
  - `removedAttachmentIds: number[]` (delete these attachments)
- **Response:** `ApiResponse<Report>`
- **Authorization:** Only the original reporter can update the report
- **Permission Check:** Backend validates that current user ID matches report.reporter.id
- **Note:** Cannot update if report status is LOCKED

### 6.5 Toggle Report Lock
- **Method:** `PATCH`
- **URL:** `/api/v1/reports/{id}/lock`
- **Description:** Lock/unlock report (Instructor only)
- **Request Body:**
```typescript
{
  isLocked: boolean
}
```
- **Response:** `ApiResponse<Report>`
- **Permission:** Instructor only

---

## 7. Comment Management

### Comment Data Structure
```typescript
{
  id: number,
  content: string,
  createdDate: Date,
  commenter: BaseUser  // User who created the comment
}
```

### 7.1 Get Comments by Report
- **Method:** `GET`
- **URL:** `/api/v1/reports/{reportId}/comments`
- **Description:** Get all comments for a report
- **Response:** `ApiResponse<Comment[]>`
- **Note:** Usually accessed through report detail endpoint, not directly

### 7.2 Add Comment to Report
- **Method:** `POST`
- **URL:** `/api/v1/reports/{reportId}/comments`
- **Description:** Add a comment to a report
- **Request Body:**
```typescript
{
  content: string,
  mentions: number[]  // User IDs mentioned in the comment
}
```
- **Response:** `ApiResponse<Comment>`
- **Authorization:** Uses JWT token to identify the commenter (current user)
- **Note:** Commenter is automatically set from the authenticated user's token

### 7.3 Delete Comment
- **Method:** `DELETE`
- **URL:** `/api/v1/comments/{commentId}`
- **Description:** Delete a comment (own comment only)
- **Response:** `ApiResponse<void>`
- **Permission Check:** Backend validates that current user ID matches comment.commenter.id
- **Note:** Only the comment creator can delete their own comment

---

## 8. Notification Management

### Notification Data Structure
```typescript
{
  id: number,
  title: string,
  message: string,
  isRead: boolean,
  createdDate: Date,
  link: string,
  project: {
    id: number,
    title: string
  }
}
```

### NotificationListResponse
```typescript
{
  totalCount: number,
  projectNotifications: Array<{
    projectId: number,
    projectTitle: string,
    notifications: Notification[]
  }>
}
```

### 8.1 Get User Notifications
- **Method:** `GET`
- **URL:** `/api/v1/notifications`
- **Description:** Get all notifications for current user
- **Response:** `ApiResponse<NotificationListResponse>`

### 8.2 Mark Notification as Read
- **Method:** `PATCH`
- **URL:** `/api/v1/notifications/{id}/read`
- **Description:** Mark a notification as read
- **Response:** `ApiResponse<Notification>`

### 8.3 Mark All Notifications as Read
- **Method:** `PATCH`
- **URL:** `/api/v1/notifications/read-all`
- **Description:** Mark all notifications as read
- **Response:** `ApiResponse<void>`

---

## 9. Firebase Device Token Management

### 9.1 Register Device Token
- **Method:** `POST`
- **URL:** `/api/v1/device-tokens/register`
- **Description:** Register FCM device token for push notifications
- **Request Body:**
```typescript
{
  fcmToken: string,
  deviceType: "WEB" | "ANDROID" | "IOS",
  deviceInfo: string  // e.g., navigator.userAgent
}
```
- **Response:** `ApiResponse<void>`

### 9.2 Delete Device Token
- **Method:** `DELETE`
- **URL:** `/api/v1/device-tokens/{fcmToken}`
- **Description:** Remove device token (e.g., on logout)
- **Response:** `ApiResponse<void>`

### 9.3 Get My Device Tokens
- **Method:** `GET`
- **URL:** `/api/v1/device-tokens/my-tokens`
- **Description:** Get all registered device tokens for current user
- **Response:** `ApiResponse<DeviceToken[]>`
```typescript
DeviceToken: {
  id: number,
  fcmToken: string,
  deviceType: string,
  deviceInfo: string,
  createdAt: string
}
```

### 9.4 Send Test Notification
- **Method:** `POST`
- **URL:** `/api/v1/device-tokens/test-notification`
- **Description:** Send a test notification to user's devices
- **Response:** `ApiResponse<void>`

---

## 10. Semester & Faculty Management

### 10.1 Get Available Years
- **Method:** `GET`
- **URL:** `/api/v1/semesters/years`
- **Description:** Get list of available academic years
- **Response:** `ApiResponse<number[]>`

### 10.2 Get Available Faculties
- **Method:** `GET`
- **URL:** `/api/v1/faculties`
- **Description:** Get list of all faculties
- **Response:** `ApiResponse<string[]>`

---

## 11. File/Attachment Management

### Attachment Data Structure
```typescript
{
  id: number,
  originalFilename: string,
  storedFilename: string,
  fileSize: number,
  fileType: string,
  storageUrl: string
}
```

### 11.1 Upload File
- **Method:** `POST`
- **URL:** `/api/v1/files/upload`
- **Description:** Upload a file to cloud storage
- **Content-Type:** `multipart/form-data`
- **Request Body:** `file: File`
- **Response:** `ApiResponse<Attachment>`

### 11.2 Download File - Phần này để download qua URL từ cloud, không cần implement
- **Method:** `GET`
- **URL:** `/api/v1/files/{attachmentId}/download`
- **Description:** Download a file
- **Response:** File stream

### 11.3 Delete File
- **Method:** `DELETE`
- **URL:** `/api/v1/files/{attachmentId}`
- **Description:** Delete a file from storage
- **Response:** `ApiResponse<void>`

---

## Authentication & Authorization Notes

### Request Headers
All authenticated requests must include:
```
Authorization: Bearer {accessToken}
```

### Token Refresh
- Access tokens should be automatically refreshed using `/api/v1/auth/refresh`
- Implement axios interceptors to handle 401 responses and retry with refreshed token

### Role-Based Access Control
- **Student:** Can create/edit milestones, tasks, and reports (if not locked)
- **Instructor:** Can create/edit projects, lock/unlock content, add comments
- Specific endpoints have role restrictions as noted in descriptions

---

## Error Handling

### Standard Error Response
```typescript
{
  status: number,  // HTTP status code
  message: string,  // Error message
  errorCode?: string,  // Application-specific error code
  data: null
}
```

### Common HTTP Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (not authenticated)
- `403` - Forbidden (not authorized for this action)
- `404` - Not Found
- `500` - Internal Server Error

---

## Notes for Backend Implementation

1. **Pagination**: Removed.

2. **Lazy Loading**: Projects should only load current batch by default. Past projects are only loaded when explicitly filtered

3. **Security & Authorization**: 
   - All project endpoints must verify user has permission (instructor owns it OR student is a member)
   - Only load data user has access to - never return projects/milestones/tasks/reports user shouldn't see
   - Filter at database level, not application level

4. **File Upload Size Limits**: Define maximum file size for uploads (recommended: 10-50MB)

5. **Date Format**: All dates should be in ISO 8601 format (e.g., `2025-01-15T10:30:00Z`)

6. **CORS**: Ensure CORS is properly configured for frontend origin

7. **Rate Limiting**: Consider implementing rate limiting on public endpoints

8. **WebSocket/SSE**: For real-time notifications, consider implementing WebSocket or Server-Sent Events

9. **Search Optimization**: Implement efficient search with database indexing on frequently queried fields

10. **Cascade Deletion**: Define cascade behavior when deleting projects, milestones, tasks (soft delete recommended)

11. **Validation**: Implement server-side validation matching frontend validation rules

12. **Transaction Management**: Ensure proper transaction handling for multi-step operations (e.g., project creation with members)

---

## Summary Statistics

- **Total Endpoints:** 60+
- **Modules:** 11
- **HTTP Methods Used:** GET, POST, PUT, PATCH, DELETE
- **Authentication Required:** All endpoints except login/register
- **File Upload Endpoints:** 3
- **Real-time Features:** Notifications (Firebase Cloud Messaging)

---

**Document Version:** 1.0  
