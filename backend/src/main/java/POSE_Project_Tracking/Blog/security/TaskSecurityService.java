package POSE_Project_Tracking.Blog.security;

import POSE_Project_Tracking.Blog.entity.Comment;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.repository.CommentRepository;
import POSE_Project_Tracking.Blog.repository.ReportRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Security service for task-level authorization checks
 */
@Service("taskSecurityService")
@RequiredArgsConstructor
public class TaskSecurityService {

    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final SecurityUtil securityUtil;

    /**
     * Check if current user is assigned to the task
     */
    public boolean isTaskAssignee(Long taskId) {
        try {
            User currentUser = securityUtil.getCurrentUser();
            Task task = taskRepository.findById(taskId).orElse(null);
            
            if (task == null) {
                return false;
            }
            
            return task.getAssignedUsers().stream()
                    .anyMatch(user -> user.getId().equals(currentUser.getId()));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user can modify the task
     * Rules: Task must not be locked and user must be project member
     */
    public boolean canModifyTask(Long taskId) {
        try {
            Task task = taskRepository.findById(taskId).orElse(null);
            
            if (task == null) {
                return false;
            }
            
            // Check if locked
            if (Boolean.TRUE.equals(task.getLocked())) {
                return false;
            }
            
            return true; // Additional checks handled by @PreAuthorize on project membership
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is the author of the report
     */
    public boolean isReportAuthor(Long reportId) {
        try {
            User currentUser = securityUtil.getCurrentUser();
            Report report = reportRepository.findById(reportId).orElse(null);
            
            if (report == null) {
                return false;
            }
            
            return report.getAuthor().getId().equals(currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is the author of the comment
     */
    public boolean isCommentAuthor(Long commentId) {
        try {
            User currentUser = securityUtil.getCurrentUser();
            Comment comment = commentRepository.findById(commentId).orElse(null);
            
            if (comment == null) {
                return false;
            }
            
            return comment.getAuthor().getId().equals(currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }
}
