package POSE_Project_Tracking.Blog.security;

import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.ReportRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service to validate lock status of entities
 */
@Service("lockValidationService")
@RequiredArgsConstructor
public class LockValidationService {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;

    /**
     * Check if entity is locked
     * @param entityType: PROJECT, MILESTONE, TASK, REPORT
     * @param entityId: ID of the entity
     * @return true if locked, false otherwise
     */
    public boolean isLocked(String entityType, Long entityId) {
        try {
            switch (entityType.toUpperCase()) {
                case "PROJECT":
                    Project project = projectRepository.findById(entityId).orElse(null);
                    return project != null && Boolean.TRUE.equals(project.getLocked());
                    
                case "MILESTONE":
                    Milestone milestone = milestoneRepository.findById(entityId).orElse(null);
                    if (milestone != null) {
                        // Check milestone lock or parent project lock
                        boolean milestoneLocked = Boolean.TRUE.equals(milestone.getLocked());
                        boolean projectLocked = Boolean.TRUE.equals(milestone.getProject().getLocked());
                        return milestoneLocked || projectLocked;
                    }
                    return false;
                    
                case "TASK":
                    Task task = taskRepository.findById(entityId).orElse(null);
                    if (task != null) {
                        // Check task lock or parent milestone/project lock
                        boolean taskLocked = Boolean.TRUE.equals(task.getLocked());
                        boolean milestoneLock = task.getMilestone() != null && 
                                Boolean.TRUE.equals(task.getMilestone().getLocked());
                        boolean projectLock = Boolean.TRUE.equals(task.getProject().getLocked());
                        return taskLocked || milestoneLock || projectLock;
                    }
                    return false;
                    
                case "REPORT":
                    Report report = reportRepository.findById(entityId).orElse(null);
                    if (report != null) {
                        // Check report lock or parent task/milestone/project lock
                        boolean reportLocked = Boolean.TRUE.equals(report.getLocked());
                        boolean taskLock = report.getTask() != null && 
                                Boolean.TRUE.equals(report.getTask().getLocked());
                        boolean milestoneLock = report.getMilestone() != null && 
                                Boolean.TRUE.equals(report.getMilestone().getLocked());
                        boolean projectLock = report.getProject() != null && 
                                Boolean.TRUE.equals(report.getProject().getLocked());
                        return reportLocked || taskLock || milestoneLock || projectLock;
                    }
                    return false;
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user can unlock entity
     * Only instructors who are project owners can unlock
     */
    public boolean canUnlock(String entityType, Long entityId) {
        // This will be checked via @PreAuthorize with isProjectInstructor
        // This method is for additional business logic if needed
        return true;
    }
}
