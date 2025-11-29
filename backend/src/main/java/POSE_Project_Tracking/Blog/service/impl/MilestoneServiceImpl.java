package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.req.MilestoneReq;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.MilestoneMapper;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IMilestoneService;
import POSE_Project_Tracking.Blog.service.ITaskService;
import POSE_Project_Tracking.Blog.service.NotificationHelperService;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class MilestoneServiceImpl implements IMilestoneService {

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MilestoneMapper milestoneMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    @Lazy
    private ITaskService taskService;

    @Autowired
    private NotificationHelperService notificationHelperService;

    @Override
    public MilestoneRes createMilestone(MilestoneReq milestoneReq) {
        // Lấy project
        Project project = projectRepository.findById(milestoneReq.getProjectId())
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Map request to entity
        Milestone milestone = milestoneMapper.toEntity(milestoneReq, project);

        // Set createdBy từ current user
        User currentUser = securityUtil.getCurrentUser();
        milestone.setCreatedBy(currentUser);

        // Save
        milestone = milestoneRepository.save(milestone);

        return milestoneMapper.toResponse(milestone);
    }

    @Override
    public MilestoneRes updateMilestone(Long id, MilestoneReq milestoneReq) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        // Check if locked
        if (Boolean.TRUE.equals(milestone.getLocked())) {
            throw new CustomException(MILESTONE_LOCKED);
        }

        // Update only allowed fields
        milestoneMapper.updateEntityFromRequest(milestoneReq, milestone);

        milestone = milestoneRepository.save(milestone);

        return milestoneMapper.toResponse(milestone);
    }

    @Override
    public MilestoneRes getMilestoneById(Long id, String include) {
        Milestone milestone;
        
        // Check if need to include tasks
        boolean includeTasks = include != null && include.contains("tasks");
        
        if (includeTasks) {
            milestone = milestoneRepository.findByIdWithTasks(id)
                    .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
            return milestoneMapper.toResponseWithTasks(milestone);
        } else {
            milestone = milestoneRepository.findById(id)
                    .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
            return milestoneMapper.toResponse(milestone);
        }
    }

    @Override
    public MilestoneRes getMilestoneWithDetails(Long id) {
        Milestone milestone = milestoneRepository.findByIdWithProject(id)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        // Force lazy loading
        milestone.getTasks().size();

        return milestoneMapper.toResponse(milestone);
    }

    @Override
    public List<MilestoneRes> getAllMilestones() {
        return milestoneRepository.findAll().stream()
                .map(milestoneMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MilestoneRes> getMilestonesByProject(Long projectId, String include) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Check if need to include tasks
        boolean includeTasks = include != null && include.contains("tasks");
        
        if (includeTasks) {
            return milestoneRepository.findByProjectIdWithTasks(projectId).stream()
                    .map(milestoneMapper::toResponseWithTasks)
                    .collect(Collectors.toList());
        } else {
            return milestoneRepository.findByProject(project).stream()
                    .map(milestoneMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<MilestoneRes> getMilestonesByStatus(EMilestoneStatus status) {
        return milestoneRepository.findByStatus(status).stream()
                .map(milestoneMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMilestone(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        if (Boolean.TRUE.equals(milestone.getLocked())) {
            throw new CustomException(MILESTONE_LOCKED);
        }

        milestoneRepository.delete(milestone);
    }

    @Override
    public MilestoneRes toggleMilestoneLock(Long id, Boolean isLocked) {
        if (Boolean.TRUE.equals(isLocked)) {
            // Lock milestone and all children
            lockMilestoneWithChildren(id);
        } else {
            // Unlock milestone only (not children)
            Milestone milestone = milestoneRepository.findById(id)
                    .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
            
            milestone.setLocked(false);
            milestone.setLockedBy(null);
            milestone.setLockedAt(null);
            milestoneRepository.save(milestone);
        }
        
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
        return milestoneMapper.toResponse(milestone);
    }

    @Override
    public void updateMilestoneCompletion(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        Long totalTasks = (long) milestone.getTasks().size();
        if (totalTasks == 0) {
            milestone.setCompletionPercentage(0.0f);
        } else {
            Long completedTasks = taskRepository.countCompletedTasksByMilestone(id);
            float percentage = (float) completedTasks / totalTasks * 100;
            milestone.setCompletionPercentage(percentage);
        }

        milestoneRepository.save(milestone);
    }

    @Override
    public List<MilestoneRes> getOverdueMilestones() {
        LocalDateTime now = LocalDateTime.now();
        return milestoneRepository.findAll().stream()
                .filter(milestone -> milestone.getEndDate() != null &&
                        milestone.getEndDate().atStartOfDay().isBefore(now) &&
                        milestone.getStatus() != EMilestoneStatus.COMPLETED)
                .map(milestoneMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateMilestoneStatus(Long id, EMilestoneStatus status) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        if (Boolean.TRUE.equals(milestone.getLocked())) {
            throw new CustomException(MILESTONE_LOCKED);
        }

        milestone.setStatus(status);
        milestoneRepository.save(milestone);
    }

    @Override
    public void lockMilestoneWithChildren(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        User currentUser = securityUtil.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        // Lock milestone only
        milestone.setLocked(true);
        milestone.setLockedBy(currentUser);
        milestone.setLockedAt(now);
        milestoneRepository.save(milestone);

        // ✅ NOTIFICATION: Milestone bị khóa
        try {
            String title = "Milestone bị khóa";
            String message = String.format("Milestone \"%s\" đã bị khóa bởi giảng viên", milestone.getTitle());
            
            notificationHelperService.createNotificationsForStudentsOnly(
                milestone.getProject(),
                title,
                message,
                ENotificationType.MILESTONE_LOCKED,
                milestone.getId(),
                "MILESTONE",
                currentUser
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Delegate to TaskService to lock all tasks (and their children)
        if (milestone.getTasks() != null) {
            milestone.getTasks().forEach(task -> {
                taskService.lockTaskWithChildren(task.getId());
            });
        }
    }
}
