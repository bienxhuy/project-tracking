package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.ProjectMapper;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IProjectService;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public ProjectRes createProject(ProjectReq projectReq) {
        // Lấy instructor
        User instructor = userRepository.findById(projectReq.getInstructorId())
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        // Map request to entity
        Project project = projectMapper.toEntity(projectReq, instructor);

        // Set createdBy từ current user
        User currentUser = securityUtil.getCurrentUser();
        project.setCreatedBy(currentUser);

        // Save
        project = projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectRes updateProject(Long id, ProjectReq projectReq) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Check if locked
        if (Boolean.TRUE.equals(project.getLocked())) {
            throw new CustomException(PROJECT_LOCKED);
        }

        // Update only allowed fields
        projectMapper.updateEntityFromRequest(projectReq, project);

        project = projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectRes getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectRes getProjectWithDetails(Long id) {
        Project project = projectRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Force lazy loading
        project.getMilestones().size();
        project.getTasks().size();

        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectRes> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getProjectsByInstructor(Long instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return projectRepository.findByInstructor(instructor).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getProjectsByStatus(EProjectStatus status) {
        return projectRepository.findByStatus(status).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getProjectsByYearAndSemester(Integer year, Integer semester) {
        return projectRepository.findByYearAndSemester(year, semester).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> searchProjects(String keyword) {
        return projectRepository.searchByKeyword(keyword).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        if (Boolean.TRUE.equals(project.getLocked())) {
            throw new CustomException(PROJECT_LOCKED);
        }

        projectRepository.delete(project);
    }

    @Override
    public void lockProject(Long id, Long userId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        project.setLocked(true);
        project.setLockedBy(user);
        project.setLockedAt(LocalDateTime.now());

        projectRepository.save(project);
    }

    @Override
    public void unlockProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        project.setLocked(false);
        project.setLockedBy(null);
        project.setLockedAt(null);

        projectRepository.save(project);
    }

    @Override
    public void updateProjectCompletion(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        Long totalTasks = (long) project.getTasks().size();
        if (totalTasks == 0) {
            project.setCompletionPercentage(0.0f);
        } else {
            Long completedTasks = taskRepository.countCompletedTasksByProject(id);
            float percentage = (float) completedTasks / totalTasks * 100;
            project.setCompletionPercentage(percentage);
        }

        projectRepository.save(project);
    }
}
