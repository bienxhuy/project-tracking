package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.AuditRevisionDTO;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Audit/History functionality
 * 
 * Provides endpoints to:
 * - View audit history of entities
 * - Track who changed what and when
 * - View entity state at specific revision or date
 * 
 * All endpoints require authentication
 */
@Slf4j
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit trail and history APIs")
public class AuditController {

    private final AuditService auditService;

    /**
     * Get complete history of a Project
     * 
     * GET /api/audit/projects/{projectId}/history
     * 
     * Returns:
     * - All revisions (changes) made to the project
     * - Who made each change
     * - When each change was made
     * - What type of change (INSERT, UPDATE, DELETE)
     */
    @GetMapping("/projects/{projectId}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get project audit history", 
               description = "Get all changes made to a project, including who changed it and when")
    public ResponseEntity<List<AuditRevisionDTO>> getProjectHistory(@PathVariable Long projectId) {
        log.info("Fetching audit history for project ID: {}", projectId);
        List<AuditRevisionDTO> history = auditService.getProjectHistory(projectId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get Project state at specific revision
     * 
     * GET /api/audit/projects/{projectId}/revisions/{revisionNumber}
     * 
     * Example: Get how project looked at revision 5
     */
    @GetMapping("/projects/{projectId}/revisions/{revisionNumber}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get project at specific revision", 
               description = "View how the project looked at a specific revision number")
    public ResponseEntity<Project> getProjectAtRevision(
            @PathVariable Long projectId,
            @PathVariable Integer revisionNumber) {
        log.info("Fetching project ID {} at revision {}", projectId, revisionNumber);
        Project project = auditService.getProjectAtRevision(projectId, revisionNumber);
        return ResponseEntity.ok(project);
    }

    /**
     * Get Project state at specific date
     * 
     * GET /api/audit/projects/{projectId}/at-date?date=2024-01-15T10:30:00
     * 
     * Example: See how project looked on Jan 15, 2024 at 10:30 AM
     */
    @GetMapping("/projects/{projectId}/at-date")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get project at specific date", 
               description = "View how the project looked at a specific date and time")
    public ResponseEntity<Project> getProjectAtDate(
            @PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("Fetching project ID {} at date {}", projectId, date);
        Project project = auditService.getProjectAtDate(projectId, date);
        return ResponseEntity.ok(project);
    }

    /**
     * Get Task history
     * 
     * GET /api/audit/tasks/{taskId}/history
     * 
     * Track:
     * - Who completed the task
     * - Status changes (TODO → IN_PROGRESS → DONE)
     * - Assignment changes
     */
    @GetMapping("/tasks/{taskId}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get task audit history", 
               description = "Get all changes made to a task (completion, assignment, etc.)")
    public ResponseEntity<List<AuditRevisionDTO>> getTaskHistory(@PathVariable Long taskId) {
        log.info("Fetching audit history for task ID: {}", taskId);
        List<AuditRevisionDTO> history = auditService.getTaskHistory(taskId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get Comment/Feedback history
     * 
     * GET /api/audit/comments/{commentId}/history
     * 
     * Track:
     * - Original feedback vs edited feedback
     * - Who edited the feedback
     * - When feedback was changed
     */
    @GetMapping("/comments/{commentId}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get comment audit history", 
               description = "Get all changes made to a comment/feedback (edits, deletions)")
    public ResponseEntity<List<AuditRevisionDTO>> getCommentHistory(@PathVariable Long commentId) {
        log.info("Fetching audit history for comment ID: {}", commentId);
        List<AuditRevisionDTO> history = auditService.getCommentHistory(commentId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get Report history (including grade changes)
     * 
     * GET /api/audit/reports/{reportId}/history
     * 
     * Track:
     * - Grade changes (teacher changed grade from 8.5 to 9.0)
     * - Report edits
     * - Status changes
     */
    @GetMapping("/reports/{reportId}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get report audit history", 
               description = "Get all changes made to a report (grade changes, edits, etc.)")
    public ResponseEntity<List<AuditRevisionDTO>> getReportHistory(@PathVariable Long reportId) {
        log.info("Fetching audit history for report ID: {}", reportId);
        List<AuditRevisionDTO> history = auditService.getReportHistory(reportId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get Milestone history
     * 
     * GET /api/audit/milestones/{milestoneId}/history
     * 
     * Track:
     * - Milestone status changes (PENDING → IN_PROGRESS → COMPLETED)
     * - Completion percentage updates
     * - Description changes
     */
    @GetMapping("/milestones/{milestoneId}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get milestone audit history", 
               description = "Get all changes made to a milestone (status, completion, etc.)")
    public ResponseEntity<List<AuditRevisionDTO>> getMilestoneHistory(@PathVariable Long milestoneId) {
        log.info("Fetching audit history for milestone ID: {}", milestoneId);
        List<AuditRevisionDTO> history = auditService.getMilestoneHistory(milestoneId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get all changes made by a specific user
     * 
     * GET /api/audit/users/{username}/changes
     * 
     * Example: See all changes made by user "john@example.com"
     */
    @GetMapping("/users/{username}/changes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    @Operation(summary = "Get changes by user", 
               description = "Get all changes made by a specific user (admin/instructor only)")
    public ResponseEntity<List<AuditRevisionDTO>> getChangesByUser(@PathVariable String username) {
        log.info("Fetching audit changes by user: {}", username);
        List<AuditRevisionDTO> changes = auditService.getChangesByUser(username);
        return ResponseEntity.ok(changes);
    }

    /**
     * Get changes within a date range
     * 
     * GET /api/audit/changes?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     * 
     * Example: Get all changes in January 2024
     */
    @GetMapping("/changes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    @Operation(summary = "Get changes in date range", 
               description = "Get all changes within a date range (admin/instructor only)")
    public ResponseEntity<List<AuditRevisionDTO>> getChangesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching audit changes between {} and {}", startDate, endDate);
        List<AuditRevisionDTO> changes = auditService.getChangesBetweenDates(startDate, endDate);
        return ResponseEntity.ok(changes);
    }

    /**
     * Get complete history of a ProjectMember
     * 
     * GET /api/audit/project-members/{projectMemberId}/history
     * 
     * Track:
     * - Role changes (STUDENT → LEADER)
     * - Active status changes
     * - Join date changes
     */
    @GetMapping("/project-members/{projectMemberId}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get project member audit history", 
               description = "Get all changes made to a project member (role, status, etc.)")
    public ResponseEntity<List<AuditRevisionDTO>> getProjectMemberHistory(@PathVariable Long projectMemberId) {
        log.info("Fetching audit history for project member ID: {}", projectMemberId);
        List<AuditRevisionDTO> history = auditService.getProjectMemberHistory(projectMemberId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get complete history of an Attachment
     * 
     * GET /api/audit/attachments/{attachmentId}/history
     * 
     * Track:
     * - File uploads (who uploaded, when)
     * - File deletions
     * - File metadata changes
     */
    @GetMapping("/attachments/{attachmentId}/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get attachment audit history", 
               description = "Get all changes made to an attachment (upload, delete, etc.)")
    public ResponseEntity<List<AuditRevisionDTO>> getAttachmentHistory(@PathVariable Long attachmentId) {
        log.info("Fetching audit history for attachment ID: {}", attachmentId);
        List<AuditRevisionDTO> history = auditService.getAttachmentHistory(attachmentId);
        return ResponseEntity.ok(history);
    }
}
