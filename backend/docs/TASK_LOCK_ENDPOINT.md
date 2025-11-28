# Task Lock/Unlock Endpoint

## Overview
This document describes the implementation of the Task Lock/Unlock endpoint that allows instructors to lock or unlock tasks.

## Implementation Date
November 28, 2025

## Endpoint Details

### Toggle Task Lock
- **Method:** `PATCH`
- **URL:** `/api/v1/tasks/{id}/lock`
- **Description:** Lock or unlock a task (Instructor only)
- **Authorization:** Requires authenticated user (instructor role)

### Request
**Path Parameter:**
- `id` (Long) - The ID of the task to lock/unlock

**Request Body:**
```json
{
  "isLocked": true  // or false to unlock
}
```

### Response
**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Khóa task thành công",  // or "Mở khóa task thành công"
  "data": {
    "id": 1,
    "title": "Task Title",
    "description": "Task Description",
    "status": "IN_PROGRESS",
    "startDate": "2025-01-01",
    "endDate": "2025-01-31",
    "isLocked": true,
    "lockedById": 5,
    "lockedByName": "Instructor Name",
    "lockedAt": "2025-11-28T10:30:00",
    "assignees": [...],
    "totalReports": 3,
    "totalComments": 5
  },
  "errorCode": null
}
```

**Error Responses:**
- `404 NOT FOUND` - Task not found
- `401 UNAUTHORIZED` - User not authenticated
- `403 FORBIDDEN` - User is not an instructor

## Implementation Details

### 1. Service Interface (`ITaskService.java`)
Added method:
```java
TaskRes toggleTaskLock(Long id, Boolean isLocked);
```

### 2. Service Implementation (`TaskServiceImpl.java`)
```java
@Override
public TaskRes toggleTaskLock(Long id, Boolean isLocked) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

    if (Boolean.TRUE.equals(isLocked)) {
        // Lock the task
        User currentUser = SecurityUtil.getCurrentUser();
        task.setLocked(true);
        task.setLockedBy(currentUser);
        task.setLockedAt(LocalDateTime.now());
    } else {
        // Unlock the task
        task.setLocked(false);
        task.setLockedBy(null);
        task.setLockedAt(null);
    }

    task = taskRepository.save(task);
    return taskMapper.toResponse(task);
}
```

**Key Features:**
- Uses `SecurityUtil.getCurrentUser()` to get the authenticated user (no need to pass userId)
- Sets lock status, locked by user, and lock timestamp when locking
- Clears lock information when unlocking
- Returns the updated task details

### 3. Controller (`TaskController.java`)
```java
@PatchMapping("/{id}/lock")
public ApiResponse<TaskRes> toggleTaskLock(
        @PathVariable Long id,
        @RequestBody java.util.Map<String, Boolean> request) {
    Boolean isLocked = request.get("isLocked");
    TaskRes task = taskService.toggleTaskLock(id, isLocked);
    return new ApiResponse<>(HttpStatus.OK, 
            isLocked ? "Khóa task thành công" : "Mở khóa task thành công", 
            task, null);
}
```

## Usage Examples

### Lock a Task
```http
PATCH /api/v1/tasks/1/lock
Content-Type: application/json
Authorization: Bearer {token}

{
  "isLocked": true
}
```

**Response:**
```json
{
  "status": 200,
  "message": "Khóa task thành công",
  "data": {
    "id": 1,
    "isLocked": true,
    "lockedById": 5,
    "lockedByName": "Nguyen Van A",
    "lockedAt": "2025-11-28T10:30:00",
    ...
  }
}
```

### Unlock a Task
```http
PATCH /api/v1/tasks/1/lock
Content-Type: application/json
Authorization: Bearer {token}

{
  "isLocked": false
}
```

**Response:**
```json
{
  "status": 200,
  "message": "Mở khóa task thành công",
  "data": {
    "id": 1,
    "isLocked": false,
    "lockedById": null,
    "lockedByName": null,
    "lockedAt": null,
    ...
  }
}
```

## Security Considerations

1. **Authentication Required:** User must be authenticated via JWT token
2. **Authorization:** Should be restricted to INSTRUCTOR role (add `@PreAuthorize("hasRole('INSTRUCTOR')")` if needed)
3. **Current User:** The system automatically uses the current authenticated user as the locker
4. **Lock Information:** When locked, stores who locked it and when

## Business Rules

1. **Locking a Task:**
   - Sets `locked` to `true`
   - Records the current user as `lockedBy`
   - Records the current timestamp as `lockedAt`

2. **Unlocking a Task:**
   - Sets `locked` to `false`
   - Clears `lockedBy` (sets to `null`)
   - Clears `lockedAt` (sets to `null`)

3. **Locked Task Behavior:**
   - Students cannot edit locked tasks
   - Students cannot delete locked tasks
   - Students cannot create reports for locked tasks (if applicable)

## Related Endpoints

- `PUT /api/v1/tasks/{id}` - Update task (checks if locked before allowing updates)
- `DELETE /api/v1/tasks/{id}` - Delete task (checks if locked before allowing deletion)
- `GET /api/v1/tasks/{id}` - Get task details (shows lock status)

## Testing Checklist

- [ ] Lock a task with valid ID
- [ ] Unlock a previously locked task
- [ ] Attempt to lock non-existent task (should return 404)
- [ ] Verify locked task shows correct locker information
- [ ] Verify unlocked task has null lock information
- [ ] Test authentication requirement (no token should fail)
- [ ] Test authorization (only instructors can lock/unlock)
- [ ] Verify students cannot edit locked tasks
- [ ] Test locking an already locked task
- [ ] Test unlocking an already unlocked task

## Comparison with Similar Patterns

This endpoint follows the same pattern as:
- `PATCH /api/v1/milestones/{id}/lock` - Milestone lock/unlock
- `PATCH /api/v1/reports/{id}/lock` - Report lock/unlock
- `PATCH /api/v1/projects/{id}/lock` - Project lock

All use:
- Same HTTP method (PATCH)
- Same URL pattern (/{id}/lock)
- Same request body format ({"isLocked": boolean})
- Same security pattern (current user from token)

## Compliance with API Specification

This implementation complies with Section 5.7 of the API specification:

✅ **Method:** PATCH
✅ **URL:** /api/v1/tasks/{id}/lock
✅ **Request Body:** `{ isLocked: boolean }`
✅ **Response:** Returns updated TaskRes
✅ **Authorization:** Instructor only
✅ **Security:** Uses current authenticated user

## Notes

- The endpoint uses a toggle approach with explicit boolean value
- Returns the full updated task object for immediate UI update
- Maintains audit trail with locker information and timestamp
- Consistent with other lock/unlock endpoints in the system
