package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.config.CacheConfig;
import POSE_Project_Tracking.Blog.dto.req.TaskReq;
import POSE_Project_Tracking.Blog.dto.res.TaskRes;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.TaskMapper;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IMilestoneService;
import POSE_Project_Tracking.Blog.service.IProjectService;
import POSE_Project_Tracking.Blog.service.ITaskService;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IMilestoneService milestoneService;

    @Override
    public TaskRes createTask(TaskReq taskReq) {
        // Lấy project
        Project project = projectRepository.findById(taskReq.getProjectId())
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Lấy milestone (nếu có)
        Milestone milestone = null;
        if (taskReq.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(taskReq.getMilestoneId())
                    .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
        }

        // Lấy assignees (nếu có)
        List<User> assignedUsers = new ArrayList<>();
        if (taskReq.getAssigneeIds() != null && !taskReq.getAssigneeIds().isEmpty()) {
            assignedUsers = userRepository.findAllById(taskReq.getAssigneeIds());
            if (assignedUsers.size() != taskReq.getAssigneeIds().size()) {
                throw new CustomException(USER_NON_EXISTENT);
            }
        }

        // Map request to entity
        Task task = taskMapper.toEntity(taskReq, project, assignedUsers);
        task.setMilestone(milestone);
        task.setStartDate(taskReq.getStartDate());
        task.setEndDate(taskReq.getEndDate());

        // Set createdBy từ current user
        User currentUser = securityUtil.getCurrentUser();
        task.setCreatedBy(currentUser);

        // Save
        task = taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskRes updateTask(Long id, TaskReq taskReq) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        // Check if locked
        if (Boolean.TRUE.equals(task.getLocked())) {
            throw new CustomException(TASK_LOCKED);
        }

        // Update only allowed fields
        taskMapper.updateEntityFromRequest(taskReq, task);

        task = taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskRes getTaskById(Long id, String include) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        // Check if reports should be included
        boolean includeReports = include != null && include.contains("reports");
        
        if (includeReports) {
            // Force lazy loading of reports
            task.getReports().size();
            return taskMapper.toResponseWithReports(task);
        }

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskRes getTaskWithDetails(Long id) {
        Task task = taskRepository.findByIdWithProjectAndMilestone(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        // Force lazy loading
        task.getComments().size();
        task.getAttachments().size();

        return taskMapper.toResponse(task);
    }

    @Override
    public List<TaskRes> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskRes> getTasksByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        return taskRepository.findByProject(project).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskRes> getTasksByMilestone(Long milestoneId, String include) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        // Check if reports should be included
        boolean includeReports = include != null && include.contains("reports");

        return taskRepository.findByMilestone(milestone).stream()
                .map(task -> {
                    if (includeReports) {
                        // Force lazy loading of reports
                        task.getReports().size();
                        return taskMapper.toResponseWithReports(task);
                    }
                    return taskMapper.toResponse(task);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskRes> getTasksByAssignee(Long assigneeId) {
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return taskRepository.findByAssignedUsersContaining(assignee).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskRes> getTasksByStatus(ETaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskRes> searchTasks(String keyword) {
        return taskRepository.searchByKeyword(keyword).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.MILESTONE_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.TASK_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.DASHBOARD_STATS_CACHE, allEntries = true)
    })
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        if (Boolean.TRUE.equals(task.getLocked())) {
            throw new CustomException(TASK_LOCKED);
        }

        // Store IDs before deleting
        Long projectId = task.getProject().getId();
        Long milestoneId = task.getMilestone() != null ? task.getMilestone().getId() : null;

        taskRepository.delete(task);

        // Update completion percentages after deletion
        projectService.updateProjectCompletion(projectId);
        if (milestoneId != null) {
            milestoneService.updateMilestoneCompletion(milestoneId);
        }
    }

    @Override
    public void lockTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        task.setLocked(true);
        task.setLockedBy(user);
        task.setLockedAt(LocalDateTime.now());

        taskRepository.save(task);
    }

    @Override
    public void unlockTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        task.setLocked(false);
        task.setLockedBy(null);
        task.setLockedAt(null);

        taskRepository.save(task);
    }

    @Override
    public TaskRes toggleTaskLock(Long id, Boolean isLocked) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        if (Boolean.TRUE.equals(isLocked)) {
            // Lock the task
            User currentUser = securityUtil.getCurrentUser();
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

    @Override
    public List<TaskRes> getTasksByUser(Long userId) {
        // Alias for getTasksByAssignee
        return getTasksByAssignee(userId);
    }

    @Override
    public List<TaskRes> getOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findAll().stream()
                .filter(task -> task.getEndDate() != null && 
                       task.getEndDate().atStartOfDay().isBefore(now) &&
                       task.getStatus() != ETaskStatus.COMPLETED)
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskRes> getOverdueTasksByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));
        
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findByAssignedUsersContaining(user).stream()
                .filter(task -> task.getEndDate() != null && 
                       task.getEndDate().atStartOfDay().isBefore(now) &&
                       task.getStatus() != ETaskStatus.COMPLETED)
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void assignTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        if (Boolean.TRUE.equals(task.getLocked())) {
            throw new CustomException(TASK_LOCKED);
        }

        // Add user to assigned users if not already present
        if (task.getAssignedUsers() == null) {
            task.setAssignedUsers(new ArrayList<>());
        }
        if (!task.getAssignedUsers().contains(user)) {
            task.getAssignedUsers().add(user);
        }
        taskRepository.save(task);
    }

    @Override
    public void updateTaskStatus(Long id, ETaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        if (Boolean.TRUE.equals(task.getLocked())) {
            throw new CustomException(TASK_LOCKED);
        }

        task.setStatus(status);
        taskRepository.save(task);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.MILESTONE_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.TASK_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.DASHBOARD_STATS_CACHE, allEntries = true)
    })
    public void markTaskAsCompleted(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        if (Boolean.TRUE.equals(task.getLocked())) {
            throw new CustomException(TASK_LOCKED);
        }

        task.setStatus(ETaskStatus.COMPLETED);
        taskRepository.save(task);

        // Update completion percentages
        Long projectId = task.getProject().getId();
        Long milestoneId = task.getMilestone() != null ? task.getMilestone().getId() : null;

        projectService.updateProjectCompletion(projectId);
        if (milestoneId != null) {
            milestoneService.updateMilestoneCompletion(milestoneId);
        }
    }
}
