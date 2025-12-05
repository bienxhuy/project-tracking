package POSE_Project_Tracking.Blog.security;

import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectMemberRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.ReportRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Security service for project-level authorization checks
 */
@Service("projectSecurityService")
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;
    private final SecurityUtil securityUtil;

    /**
     * Check if current user is a member of the project
     */
    public boolean isProjectMember(Long projectId) {
        try {
            User currentUser = securityUtil.getCurrentUser();
            return projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is the instructor of the project
     */
    public boolean isProjectInstructor(Long projectId) {
        try {
            User currentUser = securityUtil.getCurrentUser();
            
            // Must be INSTRUCTOR role
            if (!EUserRole.INSTRUCTOR.equals(currentUser.getRole())) {
                return false;
            }
            
            // Must be the project creator (instructor)
            Project project = projectRepository.findById(projectId).orElse(null);
            if (project == null) {
                return false;
            }
            
            return project.getCreatedBy().getId().equals(currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is the owner (creator) of the project
     */
    public boolean isProjectOwner(Long projectId) {
        try {
            User currentUser = securityUtil.getCurrentUser();
            Project project = projectRepository.findById(projectId).orElse(null);
            
            if (project == null) {
                return false;
            }
            
            return project.getCreatedBy().getId().equals(currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user can view user profile
     * Rules: 
     * - ADMIN can view all
     * - INSTRUCTOR/STUDENT can view own profile or project members
     */
    public boolean canViewUserProfile(Long userId) {
        try {
            User currentUser = securityUtil.getCurrentUser();
            
            // Admin can view all
            if (EUserRole.ADMIN.equals(currentUser.getRole())) {
                return true;
            }
            
            // Can view own profile
            if (currentUser.getId().equals(userId)) {
                return true;
            }
            
            // Can view profile if user is in same project
            return projectMemberRepository.existsCommonProject(currentUser.getId(), userId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is member of milestone's project
     */
    public boolean isMilestoneMember(Long milestoneId) {
        try {
            Milestone milestone = milestoneRepository.findById(milestoneId).orElse(null);
            if (milestone == null || milestone.getProject() == null) {
                return false;
            }
            return isProjectMember(milestone.getProject().getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is member of task's project
     */
    public boolean isTaskMember(Long taskId) {
        try {
            Task task = taskRepository.findById(taskId).orElse(null);
            if (task == null || task.getProject() == null) {
                return false;
            }
            return isProjectMember(task.getProject().getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is member of report's project
     */
    public boolean isReportMember(Long reportId) {
        try {
            Report report = reportRepository.findById(reportId).orElse(null);
            if (report == null || report.getProject() == null) {
                return false;
            }
            return isProjectMember(report.getProject().getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is instructor of milestone's project
     */
    public boolean isMilestoneInstructor(Long milestoneId) {
        try {
            Milestone milestone = milestoneRepository.findById(milestoneId).orElse(null);
            if (milestone == null || milestone.getProject() == null) {
                return false;
            }
            return isProjectInstructor(milestone.getProject().getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is instructor of task's project
     */
    public boolean isTaskInstructor(Long taskId) {
        try {
            Task task = taskRepository.findById(taskId).orElse(null);
            if (task == null || task.getProject() == null) {
                return false;
            }
            return isProjectInstructor(task.getProject().getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if current user is instructor of report's project
     */
    public boolean isReportInstructor(Long reportId) {
        try {
            Report report = reportRepository.findById(reportId).orElse(null);
            if (report == null || report.getProject() == null) {
                return false;
            }
            return isProjectInstructor(report.getProject().getId());
        } catch (Exception e) {
            return false;
        }
    }
}
