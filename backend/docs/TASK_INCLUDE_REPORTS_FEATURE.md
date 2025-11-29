# Task Management - Include Reports Feature

## Overview
This document describes the implementation of the `include` query parameter for Task Management endpoints, allowing clients to optionally include reports in task responses.

## Implementation Date
November 28, 2025

## Changes Made

### 1. TaskRes.java
- **Added Field:** `List<ReportRes> reports` - Optional list of reports associated with the task
- **Location:** Between `assignedUsers` and statistics fields
- **Purpose:** Hold report data when requested via `include` parameter

### 2. TaskMapper.java
- **Added Dependency:** `uses = {ReportMapper.class}` to enable report mapping
- **New Method:** `toResponseWithReports(Task task)` - Maps task with reports included
- **Modified Method:** `toResponse(Task task)` - Updated to ignore reports field
- **Mapping Strategy:** 
  - `toResponse()`: Sets `reports` to `@Mapping(target = "reports", ignore = true)`
  - `toResponseWithReports()`: Sets `reports` to `@Mapping(target = "reports", source = "task.reports")`

### 3. ITaskService.java
- **Updated Methods:**
  - `getTaskById(Long id, String include)` - Added `include` parameter
  - `getTasksByMilestone(Long milestoneId, String include)` - Added `include` parameter

### 4. TaskServiceImpl.java
- **Updated `getTaskById()`:**
  - Checks if `include` contains "reports"
  - Forces lazy loading of reports collection if needed
  - Returns appropriate mapper method result

- **Updated `getTasksByMilestone()`:**
  - Checks if `include` contains "reports" for each task
  - Forces lazy loading of reports collection if needed
  - Maps each task with appropriate method

### 5. TaskController.java
- **Updated Endpoint:** `GET /api/v1/tasks/{id}`
  - Added `@RequestParam(required = false) String include` parameter
  - Passes parameter to service layer

### 6. MilestoneController.java
- **Added Dependencies:**
  - Imported `TaskRes` and `ITaskService`
  - Autowired `ITaskService`

- **New Endpoint:** `GET /api/v1/milestones/{milestoneId}/tasks`
  - Query Parameter: `include` (optional)
  - Returns list of tasks for a milestone
  - Supports `include=reports` to include report data

## API Usage Examples

### Get Task by ID (without reports)
```http
GET /api/v1/tasks/1
```
**Response:**
```json
{
  "status": 200,
  "message": "Lấy thông tin task thành công",
  "data": {
    "id": 1,
    "title": "Task Title",
    "description": "Task Description",
    "status": "IN_PROGRESS",
    "assignees": [...],
    "totalReports": 5,
    "reports": null  // Not included
  }
}
```

### Get Task by ID (with reports)
```http
GET /api/v1/tasks/1?include=reports
```
**Response:**
```json
{
  "status": 200,
  "message": "Lấy thông tin task thành công",
  "data": {
    "id": 1,
    "title": "Task Title",
    "description": "Task Description",
    "status": "IN_PROGRESS",
    "assignees": [...],
    "totalReports": 5,
    "reports": [
      {
        "id": 1,
        "title": "Report 1",
        "content": "...",
        "status": "SUBMITTED",
        ...
      },
      ...
    ]
  }
}
```

### Get Tasks by Milestone (without reports)
```http
GET /api/v1/milestones/1/tasks
```
**Response:** List of tasks without reports

### Get Tasks by Milestone (with reports)
```http
GET /api/v1/milestones/1/tasks?include=reports
```
**Response:** List of tasks with reports array populated

## Benefits

1. **Performance Optimization:** Clients can avoid loading unnecessary report data when not needed
2. **Flexible API:** Single endpoint serves multiple use cases
3. **Consistency:** Matches the pattern already used in Milestone endpoints with `include=tasks`
4. **Lazy Loading:** Reports are only fetched from database when explicitly requested

## Compliance with API Specification

This implementation complies with Section 5 (Task Management) of the API specification:

- ✅ **5.1 Get Tasks by Milestone:** Supports `include` parameter
- ✅ **5.2 Get Task by ID:** Supports `include` parameter
- ✅ **TaskDetail Structure:** Returns task with optional reports array

## Related Files

- `TaskRes.java` - DTO with reports field
- `TaskMapper.java` - Mapping logic for reports
- `ITaskService.java` - Service interface
- `TaskServiceImpl.java` - Service implementation with include logic
- `TaskController.java` - REST endpoint for task by ID
- `MilestoneController.java` - REST endpoint for tasks by milestone

## Testing Recommendations

1. Test `GET /api/v1/tasks/{id}` without `include` parameter
2. Test `GET /api/v1/tasks/{id}?include=reports`
3. Test `GET /api/v1/milestones/{milestoneId}/tasks` without `include` parameter
4. Test `GET /api/v1/milestones/{milestoneId}/tasks?include=reports`
5. Verify lazy loading performance (reports should not be fetched unnecessarily)
6. Test with tasks that have no reports
7. Test with tasks that have multiple reports

## Notes

- The `include` parameter can be extended in the future to support multiple values (e.g., `include=reports,comments`)
- Report data is fetched using lazy loading to prevent N+1 query issues
- The implementation follows the same pattern as Milestone's `include=tasks` feature
