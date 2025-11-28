package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.AuditRevisionDTO;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.Comment;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.Attachment;
import POSE_Project_Tracking.Blog.entity.audit.CustomRevisionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for querying audit history using Hibernate Envers
 * 
 * Provides methods to:
 * - Get all revisions of an entity
 * - Get entity state at specific revision
 * - Get entity state at specific date/time
 * - Track who changed what and when
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Get all revision history for a Project
     * 
     * @param projectId The project ID
     * @return List of all revisions with metadata
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getProjectHistory(Long projectId) {
        log.info("Getting audit history for project ID: {}", projectId);
        return getEntityHistory(Project.class, projectId);
    }

    /**
     * Get all revision history for a Task
     * 
     * @param taskId The task ID
     * @return List of all revisions with metadata
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getTaskHistory(Long taskId) {
        log.info("Getting audit history for task ID: {}", taskId);
        return getEntityHistory(Task.class, taskId);
    }

    /**
     * Get all revision history for a Comment
     * 
     * @param commentId The comment ID
     * @return List of all revisions with metadata
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getCommentHistory(Long commentId) {
        log.info("Getting audit history for comment ID: {}", commentId);
        return getEntityHistory(Comment.class, commentId);
    }

    /**
     * Get all revision history for a Report
     * 
     * @param reportId The report ID
     * @return List of all revisions with metadata
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getReportHistory(Long reportId) {
        log.info("Getting audit history for report ID: {}", reportId);
        return getEntityHistory(Report.class, reportId);
    }

    /**
     * Get all revision history for a Milestone
     * 
     * @param milestoneId The milestone ID
     * @return List of all revisions with metadata
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getMilestoneHistory(Long milestoneId) {
        log.info("Getting audit history for milestone ID: {}", milestoneId);
        return getEntityHistory(Milestone.class, milestoneId);
    }

    /**
     * Generic method to get entity history
     * 
     * @param entityClass The entity class
     * @param entityId The entity ID
     * @return List of audit revisions
     */
    @Transactional(readOnly = true)
    public <T> List<AuditRevisionDTO> getEntityHistory(Class<T> entityClass, Long entityId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        // Query all revisions of the entity
        List<Object[]> revisions = auditReader.createQuery()
            .forRevisionsOfEntity(entityClass, false, true)
            .add(AuditEntity.id().eq(entityId))
            .getResultList();

        List<AuditRevisionDTO> history = new ArrayList<>();

        for (Object[] revision : revisions) {
            T entity = (T) revision[0];
            CustomRevisionEntity revisionEntity = (CustomRevisionEntity) revision[1];
            RevisionType revisionType = (RevisionType) revision[2];

            LocalDateTime timestamp = new Date(revisionEntity.getTimestamp())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

            AuditRevisionDTO dto = AuditRevisionDTO.builder()
                .revisionNumber(revisionEntity.getId())
                .timestamp(timestamp)
                .username(revisionEntity.getUsername())
                .ipAddress(revisionEntity.getIpAddress())
                .revisionType(revisionType.name())
                .entityData(entity)
                .build();

            history.add(dto);
        }

        log.info("Found {} revisions for {} ID {}", history.size(), entityClass.getSimpleName(), entityId);
        return history;
    }

    /**
     * Get Project at a specific revision
     * 
     * @param projectId The project ID
     * @param revisionNumber The revision number
     * @return Project state at that revision
     */
    @Transactional(readOnly = true)
    public Project getProjectAtRevision(Long projectId, Integer revisionNumber) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.find(Project.class, projectId, revisionNumber);
    }

    /**
     * Get Project at a specific date/time
     * 
     * @param projectId The project ID
     * @param date The date to query
     * @return Project state at that date
     */
    @Transactional(readOnly = true)
    public Project getProjectAtDate(Long projectId, LocalDateTime date) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        Date queryDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        return auditReader.find(Project.class, projectId, queryDate);
    }

    /**
     * Get all revision numbers for an entity
     * 
     * @param entityClass The entity class
     * @param entityId The entity ID
     * @return List of revision numbers
     */
    @Transactional(readOnly = true)
    public <T> List<Number> getRevisionNumbers(Class<T> entityClass, Long entityId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.getRevisions(entityClass, entityId);
    }

    /**
     * Get all changes made by a specific user
     * 
     * @param username The username
     * @return List of changes
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getChangesByUser(String username) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        
        // Query revisions by username (requires custom query)
        // This is a simplified version - in production you might want to query REVINFO table directly
        List<AuditRevisionDTO> changes = new ArrayList<>();
        
        // You can extend this to query across multiple entity types
        // For now, we'll focus on Projects as an example
        List<Object[]> projectRevisions = auditReader.createQuery()
            .forRevisionsOfEntity(Project.class, false, true)
            .getResultList();

        for (Object[] revision : projectRevisions) {
            CustomRevisionEntity revisionEntity = (CustomRevisionEntity) revision[1];
            
            if (username.equals(revisionEntity.getUsername())) {
                Project project = (Project) revision[0];
                RevisionType revisionType = (RevisionType) revision[2];

                LocalDateTime timestamp = new Date(revisionEntity.getTimestamp())
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

                AuditRevisionDTO dto = AuditRevisionDTO.builder()
                    .revisionNumber(revisionEntity.getId())
                    .timestamp(timestamp)
                    .username(revisionEntity.getUsername())
                    .ipAddress(revisionEntity.getIpAddress())
                    .revisionType(revisionType.name())
                    .entityData(project)
                    .build();

                changes.add(dto);
            }
        }

        return changes.stream()
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .collect(Collectors.toList());
    }

    /**
     * Get changes within a date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of changes
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getChangesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        
        long startTimestamp = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTimestamp = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // Query projects changed in date range
        List<Object[]> revisions = auditReader.createQuery()
            .forRevisionsOfEntity(Project.class, false, true)
            .add(AuditEntity.revisionProperty("timestamp").ge(startTimestamp))
            .add(AuditEntity.revisionProperty("timestamp").le(endTimestamp))
            .getResultList();

        List<AuditRevisionDTO> changes = new ArrayList<>();

        for (Object[] revision : revisions) {
            Project project = (Project) revision[0];
            CustomRevisionEntity revisionEntity = (CustomRevisionEntity) revision[1];
            RevisionType revisionType = (RevisionType) revision[2];

            LocalDateTime timestamp = new Date(revisionEntity.getTimestamp())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

            AuditRevisionDTO dto = AuditRevisionDTO.builder()
                .revisionNumber(revisionEntity.getId())
                .timestamp(timestamp)
                .username(revisionEntity.getUsername())
                .ipAddress(revisionEntity.getIpAddress())
                .revisionType(revisionType.name())
                .entityData(project)
                .build();

            changes.add(dto);
        }

        return changes;
    }

    /**
     * Get all revision history for a ProjectMember
     * 
     * @param projectMemberId The project member ID
     * @return List of all revisions with metadata
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getProjectMemberHistory(Long projectMemberId) {
        return getEntityHistory(ProjectMember.class, projectMemberId);
    }

    /**
     * Get all revision history for an Attachment
     * 
     * @param attachmentId The attachment ID
     * @return List of all revisions with metadata
     */
    @Transactional(readOnly = true)
    public List<AuditRevisionDTO> getAttachmentHistory(Long attachmentId) {
        return getEntityHistory(Attachment.class, attachmentId);
    }
}
