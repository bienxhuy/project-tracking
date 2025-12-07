package POSE_Project_Tracking.Blog.service.impl;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import POSE_Project_Tracking.Blog.dto.req.BulkProjectUpdateReq;
import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import static POSE_Project_Tracking.Blog.enums.ErrorCode.INSTRUCTOR_CANNOT_BE_ASSIGNED_TASK;
import static POSE_Project_Tracking.Blog.enums.ErrorCode.PROJECT_LOCKED;
import static POSE_Project_Tracking.Blog.enums.ErrorCode.PROJECT_NOT_FOUND;
import static POSE_Project_Tracking.Blog.enums.ErrorCode.USER_NON_EXISTENT;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.ProjectMapper;
import POSE_Project_Tracking.Blog.repository.ProjectMemberRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IMilestoneService;
import POSE_Project_Tracking.Blog.service.IProjectService;
import POSE_Project_Tracking.Blog.service.ITaskService;
import POSE_Project_Tracking.Blog.service.NotificationHelperService;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import POSE_Project_Tracking.Blog.util.AcademicYearUtil;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Slf4j
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
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    @Lazy
    private IMilestoneService milestoneService;

    @Autowired
    @Lazy
    private ITaskService taskService;

    @Autowired
    private NotificationHelperService notificationHelperService;

    private static boolean inspected = false;  // Chỉ inspect 1 lần

    /**
     * Debug method to inspect proxy information
     * Uses @EventListener to wait until ALL beans are created
     */
    @EventListener(ContextRefreshedEvent.class)
    public void inspectProxyDetails(ContextRefreshedEvent event) {
        // Chỉ inspect 1 lần (tránh log trung lặp)
        if (inspected) {
            return;
        }
        inspected = true;

        try {
            // Lấy bean từ ApplicationContext (lúc này đã tạo xong)
            Object bean = event.getApplicationContext().getBean(ProjectServiceImpl.class);

            System.out.println("\n========================================");
            System.out.println("🔍 SPRING PROXY INSPECTION");
            System.out.println("========================================");

            // 1. Class information
            System.out.println("\n📦 CLASS INFORMATION:");
            System.out.println("Bean class name: " + bean.getClass().getName());
            System.out.println("Simple name: " + bean.getClass().getSimpleName());
            System.out.println("Superclass: " + bean.getClass().getSuperclass().getName());

            // 2. Proxy detection
            System.out.println("\n🎭 PROXY DETECTION:");
            System.out.println("Is AOP proxy? " + AopUtils.isAopProxy(bean));
            System.out.println("Is CGLIB proxy? " + AopUtils.isCglibProxy(bean));
            System.out.println("Is JDK dynamic proxy? " + AopUtils.isJdkDynamicProxy(bean));

            // 3. Interfaces
            System.out.println("\n🔌 IMPLEMENTED INTERFACES:");
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            for (Class<?> intf : interfaces) {
                System.out.println("  - " + intf.getName());
            }

            // 4. Methods comparison
            System.out.println("\n⚙️ METHODS COUNT:");
            System.out.println("Original class methods: " + ProjectServiceImpl.class.getDeclaredMethods().length);
            System.out.println("Proxy class methods: " + bean.getClass().getDeclaredMethods().length);

            // 5. Sample method inspection
            System.out.println("\n🔍 SAMPLE METHOD INSPECTION (getProjectById):");
            Method[] proxyMethods = bean.getClass().getDeclaredMethods();
            for (Method method : proxyMethods) {
                if (method.getName().contains("getProjectById")) {
                    System.out.println("  Method: " + method.getName());
                    System.out.println("  Declaring class: " + method.getDeclaringClass().getName());
                    System.out.println("  Parameters: " + Arrays.toString(method.getParameterTypes()));
                }
            }

            // 6. Target class
            System.out.println("\n🎯 TARGET CLASS (Real Bean):");
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
            System.out.println("Target class: " + targetClass.getName());

            System.out.println("\n========================================");
            System.out.println("✅ INSPECTION COMPLETE");
            System.out.println("========================================\n");

        } catch (Exception e) {
            System.err.println("❌ Error inspecting proxy: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ProjectRes createProject(ProjectReq projectReq) {
        // Lấy current user làm instructor
        User currentUser = securityUtil.getCurrentUser();

        // Map request to entity
        Project project = projectMapper.toEntity(projectReq, currentUser);

        // Set createdBy cũng là current user
        project.setCreatedBy(currentUser);

        // Save project first to get ID
        project = projectRepository.save(project);

        // Add instructor as project member
        ProjectMember instructorMember = ProjectMember.builder()
                .project(project)
                .user(currentUser)
                .role(currentUser.getRole())
                .joinedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        projectMemberRepository.save(instructorMember);

        // Add student members if studentIds provided
        if (projectReq.getStudentIds() != null && !projectReq.getStudentIds().isEmpty()) {
            addProjectMembers(project, projectReq.getStudentIds());
            
            // Gửi notification cho các students được thêm vào project
            sendProjectAddedNotifications(project, projectReq.getStudentIds(), currentUser);
        }

        return projectMapper.toResponse(project);
    }
    
    /**
     * Gửi notification cho students khi được thêm vào project
     */
    private void sendProjectAddedNotifications(Project project, List<Long> studentIds, User instructor) {
        try {
            // Lấy thông tin các students
            List<User> students = userRepository.findAllById(studentIds);
            
            String title = "🎓 Bạn đã được thêm vào dự án mới";
            String message = String.format(
                "%s đã thêm bạn vào dự án '%s'",
                instructor.getDisplayName() != null ? instructor.getDisplayName() : instructor.getUsername(),
                project.getTitle()
            );
            
            // Gửi notification cho tất cả students được thêm vào
            notificationHelperService.createNotificationsForUsers(
                students,
                title,
                message,
                ENotificationType.PROJECT_ASSIGNED,
                project.getId(),
                "PROJECT",
                instructor
            );
            
            log.info("✅ Sent PROJECT_ASSIGNED notifications to {} students for project: {}", 
                    students.size(), project.getTitle());
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception để không ảnh hưởng đến việc tạo project
            log.error("❌ Error sending project added notifications: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR') and @projectSecurityService.isProjectInstructor(#id)")
    public ProjectRes updateProject(Long id, ProjectReq projectReq) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Check if locked
        if (Boolean.TRUE.equals(project.getLocked())) {
            throw new CustomException(PROJECT_LOCKED);
        }

        // Update only allowed fields
        projectMapper.updateEntityFromRequest(projectReq, project);

        // Update project members if studentIds provided
        if (projectReq.getStudentIds() != null) {
            // Save the instructor member before clearing
            ProjectMember instructorMember = null;
            if (project.getMembers() != null) {
                instructorMember = project.getMembers().stream()
                        .filter(member -> member.getRole() == EUserRole.INSTRUCTOR || member.getRole() == EUserRole.ADMIN)
                        .findFirst()
                        .orElse(null);
                
                // Clear all existing members (orphanRemoval = true will delete them from DB)
                project.getMembers().clear();
            }

            // Save to trigger orphan removal
            project = projectRepository.save(project);

            // Re-add the instructor if existed
            if (instructorMember != null) {
                ProjectMember newInstructorMember = ProjectMember.builder()
                        .project(project)
                        .user(instructorMember.getUser())
                        .role(instructorMember.getRole())
                        .joinedAt(instructorMember.getJoinedAt())
                        .isActive(instructorMember.getIsActive())
                        .build();
                projectMemberRepository.save(newInstructorMember);
            }

            // Add new student members
            if (!projectReq.getStudentIds().isEmpty()) {
                addProjectMembers(project, projectReq.getStudentIds());
            }
        }

        project = projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    @Override
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT') and @projectSecurityService.isProjectMember(#id)")
    public ProjectRes getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectRes getProjectWithDetails(Long id) {
        Project project = projectRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Force lazy loading of collections separately to avoid MultipleBagFetchException
        // Hibernate cannot fetch multiple bags in one query
        if (project.getMilestones() != null) {
            project.getMilestones().size();
            // Force load tasks for each milestone
            project.getMilestones().forEach(milestone -> {
                if (milestone.getTasks() != null) {
                    milestone.getTasks().size();
                }
            });
        }
        if (project.getMembers() != null) {
            project.getMembers().size();
        }
        
        return projectMapper.toResponseWithDetails(project);
    }

    @Override
    public List<ProjectRes> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getProjectsByInstructor(Long instructorId, Integer year, Integer semester, String batch) {
        // Use current academic year/semester/batch as defaults if not provided
        Integer effectiveYear = year != null ? year : AcademicYearUtil.getCurrentYear();
        Integer effectiveSemester = semester != null ? semester : AcademicYearUtil.getCurrentSemester();
        String effectiveBatch = batch != null ? batch : AcademicYearUtil.getCurrentBatch();

        return projectRepository.findByInstructorIdWithFilters(
                instructorId, effectiveYear, effectiveSemester, effectiveBatch
        ).stream()
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
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    public List<ProjectRes> searchProjects(String keyword) {
        return projectRepository.searchByKeyword(keyword).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR') and @projectSecurityService.isProjectInstructor(#id)")
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        if (Boolean.TRUE.equals(project.getLocked())) {
            throw new CustomException(PROJECT_LOCKED);
        }

        projectRepository.delete(project);
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR') and @projectSecurityService.isProjectInstructor(#id)")
    public void lockProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Get current authenticated user
        User currentUser = securityUtil.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        // Lock project only
        project.setLocked(true);
        project.setLockedBy(currentUser);
        project.setLockedAt(now);
        projectRepository.save(project);

        // ✅ NOTIFICATION: Project bị khóa
        try {
            String title = "Dự án bị khóa";
            String message = String.format("Dự án \"%s\" đã bị khóa bởi giảng viên", project.getTitle());
            
            notificationHelperService.createNotificationsForStudentsOnly(
                project,
                title,
                message,
                ENotificationType.PROJECT_LOCKED,
                project.getId(),
                "PROJECT",
                currentUser
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Delegate to MilestoneService to lock all milestones (and their children)
        if (project.getMilestones() != null) {
            project.getMilestones().forEach(milestone -> {
                milestoneService.lockMilestoneWithChildren(milestone.getId());
            });
        }

        // Delegate to TaskService to lock all tasks not in milestone (and their children)
        if (project.getTasks() != null) {
            project.getTasks().stream()
                .filter(task -> task.getMilestone() == null)
                .forEach(task -> {
                    taskService.lockTaskWithChildren(task.getId());
                });
        }
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR') and @projectSecurityService.isProjectInstructor(#id)")
    public void unlockProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        project.setLocked(false);
        project.setLockedBy(null);
        project.setLockedAt(null);

        projectRepository.save(project);
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR') and @projectSecurityService.isProjectInstructor(#id)")
    public void lockProjectContent(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Get current authenticated user
        User currentUser = securityUtil.getCurrentUser();

        project.setIsObjDesLocked(true);
        project.setLockedBy(currentUser);
        project.setLockedAt(LocalDateTime.now());

        projectRepository.save(project);

        // ✅ NOTIFICATION: Project content bị khóa
        try {
            String title = "Nội dung dự án bị khóa";
            String message = String.format("Nội dung dự án \"%s\" đã bị khóa bởi giảng viên", project.getTitle());
            
            notificationHelperService.createNotificationsForStudentsOnly(
                project,
                title,
                message,
                ENotificationType.PROJECT_CONTENT_LOCKED,
                project.getId(),
                "PROJECT",
                currentUser
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR') and @projectSecurityService.isProjectInstructor(#id)")
    public void unlockProjectContent(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        project.setIsObjDesLocked(false);

        projectRepository.save(project);
    }

    @Override
    @PreAuthorize("hasRole('STUDENT') and @projectSecurityService.isProjectMember(#id) and !@lockValidationService.isLocked('PROJECT', #id)")
    public ProjectRes updateProjectContent(Long id, String objective, String content) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Check if content is locked
        if (Boolean.TRUE.equals(project.getIsObjDesLocked())) {
            throw new CustomException(PROJECT_LOCKED, "Project objective and content are locked");
        }

        // Update only objective and content
        if (objective != null) {
            project.setObjectives(objective);
        }
        if (content != null) {
            project.setContent(content);
        }

        project = projectRepository.save(project);

        // ✅ NOTIFICATION: Objective & content được define
        try {
            User currentUser = securityUtil.getCurrentUser();
            String title = "Nội dung dự án được cập nhật";
            String message = String.format("%s đã định nghĩa nội dung cho dự án \"%s\"", 
                currentUser.getDisplayName(), project.getTitle());
            
            notificationHelperService.createNotificationsForAllProjectMembers(
                project,
                title,
                message,
                ENotificationType.PROJECT_CONTENT_DEFINED,
                project.getId(),
                "PROJECT",
                currentUser
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return projectMapper.toResponse(project);
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
            
            // Automatically update status to COMPLETED when completion reaches 100%
            if (percentage >= 100.0f) {
                project.setStatus(EProjectStatus.COMPLETED);
            }
        }

        projectRepository.save(project);
    }

    @Override
    public List<ProjectRes> getProjectsByStudent(Long studentId, Integer year, Integer semester, String batch) {
        // Use current academic year/semester/batch as defaults if not provided
        Integer effectiveYear = year != null ? year : AcademicYearUtil.getCurrentYear();
        Integer effectiveSemester = semester != null ? semester : AcademicYearUtil.getCurrentSemester();
        String effectiveBatch = batch != null ? batch : AcademicYearUtil.getCurrentBatch();

        return projectRepository.findProjectsByMemberUserIdWithFilters(
                studentId, effectiveYear, effectiveSemester, effectiveBatch
        ).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getProjectsByStudentAndStatus(Long studentId, EProjectStatus status, 
                                                          Integer year, Integer semester, String batch) {
        // Use current academic year/semester/batch as defaults if not provided
        Integer effectiveYear = year != null ? year : AcademicYearUtil.getCurrentYear();
        Integer effectiveSemester = semester != null ? semester : AcademicYearUtil.getCurrentSemester();
        String effectiveBatch = batch != null ? batch : AcademicYearUtil.getCurrentBatch();

        return projectRepository.findProjectsByMemberUserIdAndStatusWithFilters(
                studentId, status, effectiveYear, effectiveSemester, effectiveBatch
        ).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    public List<ProjectRes> getMyProjects(Integer year, Integer semester, String batch) {
        User currentUser = securityUtil.getCurrentUser();
        
        // Use current academic year/semester/batch as defaults if not provided
        Integer effectiveYear = year != null ? year : AcademicYearUtil.getCurrentYear();
        Integer effectiveSemester = semester != null ? semester : AcademicYearUtil.getCurrentSemester();
        String effectiveBatch = batch != null ? batch : AcademicYearUtil.getCurrentBatch();

        return projectRepository.findProjectsByMemberUserIdWithFilters(
                currentUser.getId(), effectiveYear, effectiveSemester, effectiveBatch
        ).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getMyProjectsByStatus(EProjectStatus status, Integer year, Integer semester, String batch) {
        User currentUser = securityUtil.getCurrentUser();
        
        // Use current academic year/semester/batch as defaults if not provided
        Integer effectiveYear = year != null ? year : AcademicYearUtil.getCurrentYear();
        Integer effectiveSemester = semester != null ? semester : AcademicYearUtil.getCurrentSemester();
        String effectiveBatch = batch != null ? batch : AcademicYearUtil.getCurrentBatch();

        return projectRepository.findProjectsByMemberUserIdAndStatusWithFilters(
                currentUser.getId(), status, effectiveYear, effectiveSemester, effectiveBatch
        ).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getAllProjectsWithFilters(Integer year, Integer semester, String batch) {
        // Use current academic year/semester/batch as defaults if not provided
        Integer effectiveYear = year != null ? year : AcademicYearUtil.getCurrentYear();
        Integer effectiveSemester = semester != null ? semester : AcademicYearUtil.getCurrentSemester();
        String effectiveBatch = batch != null ? batch : AcademicYearUtil.getCurrentBatch();

        return projectRepository.findAllWithFilters(
                effectiveYear, effectiveSemester, effectiveBatch
        ).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getAllProjectsByStudent(Long studentId) {
        return projectRepository.findProjectsByMemberUserId(studentId).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getAllProjectsByStudentAndStatus(Long studentId, EProjectStatus status) {
        return projectRepository.findProjectsByMemberUserIdAndStatus(studentId, status).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getAllMyProjects() {
        User currentUser = securityUtil.getCurrentUser();
        return projectRepository.findProjectsByMemberUserId(currentUser.getId()).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRes> getAllMyProjectsByStatus(EProjectStatus status) {
        User currentUser = securityUtil.getCurrentUser();
        return projectRepository.findProjectsByMemberUserIdAndStatus(currentUser.getId(), status).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to add student members to a project
     * @param project The project to add members to
     * @param studentIds List of student user IDs
     */
    private void addProjectMembers(Project project, List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Long studentId : studentIds) {
            // Find user by ID
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

            // Verify user is a student
            if (student.getRole() != EUserRole.STUDENT) {
                throw new CustomException(INSTRUCTOR_CANNOT_BE_ASSIGNED_TASK, 
                    "User with ID " + studentId + " is not a student and cannot be added to project");
            }

            // Create project member
            ProjectMember member = ProjectMember.builder()
                    .project(project)
                    .user(student)
                    .role(EUserRole.STUDENT)
                    .joinedAt(now)
                    .isActive(true)
                    .build();

            projectMemberRepository.save(member);
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ProjectRes updateProjectWithMilestonesAndTasks(Long id, BulkProjectUpdateReq bulkUpdateReq) {
        log.info("Starting bulk update for project ID: {}", id);
        
        // Get existing project
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));
        
        // Check if project is locked
        if (project.getLocked()) {
            throw new CustomException(PROJECT_LOCKED);
        }

        // Update project content and objectives
        project.setContent(bulkUpdateReq.getContent());
        project.setObjectives(bulkUpdateReq.getObjectives());
        project.setUpdatedAt(LocalDateTime.now());
        
        Project savedProject = projectRepository.save(project);
        log.info("Updated project content and objectives for project ID: {}", id);

        // Process milestones and tasks
        int milestoneOrder = 1;
        for (BulkProjectUpdateReq.BulkMilestoneReq milestoneReq : bulkUpdateReq.getMilestones()) {
            // Create milestone
            Milestone milestone = Milestone.builder()
                    .title(milestoneReq.getTitle())
                    .description(milestoneReq.getDescription())
                    .project(savedProject)
                    .startDate(milestoneReq.getStartDate().toLocalDate())
                    .endDate(milestoneReq.getEndDate().toLocalDate())
                    .status(EMilestoneStatus.IN_PROGRESS)
                    .orderNumber(milestoneOrder++)
                    .locked(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            Milestone savedMilestone = milestoneRepository.save(milestone);
            log.info("Created milestone: {} for project ID: {}", savedMilestone.getTitle(), id);

            // Process tasks for this milestone
            if (milestoneReq.getTasks() != null && !milestoneReq.getTasks().isEmpty()) {
                for (BulkProjectUpdateReq.BulkTaskReq taskReq : milestoneReq.getTasks()) {
                    // Create task
                    Task task = Task.builder()
                            .title(taskReq.getTitle())
                            .description(taskReq.getDescription())
                            .project(savedProject)
                            .milestone(savedMilestone)
                            .startDate(taskReq.getStartDate().toLocalDate())
                            .endDate(taskReq.getEndDate().toLocalDate())
                            .status(ETaskStatus.IN_PROGRESS)
                            .locked(false)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    
                    Task savedTask = taskRepository.save(task);
                    log.info("Created task: {} for milestone: {}", savedTask.getTitle(), savedMilestone.getTitle());

                    // Assign users to task
                    if (taskReq.getAssignees() != null && !taskReq.getAssignees().isEmpty()) {
                        for (BulkProjectUpdateReq.AssigneeReq assigneeReq : taskReq.getAssignees()) {
                            User assignee = userRepository.findById(assigneeReq.getId())
                                    .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));
                            
                            // Check if user is instructor (instructors cannot be assigned to tasks)
                            if (assignee.getRole() == EUserRole.INSTRUCTOR) {
                                log.warn("Skipping instructor {} for task assignment", assignee.getDisplayName());
                                continue;
                            }

                            // Add assignee to task
                            if (savedTask.getAssignedUsers() == null) {
                                savedTask.setAssignedUsers(new ArrayList<>());
                            }
                            savedTask.getAssignedUsers().add(assignee);
                            log.info("Assigned user: {} to task: {}", assignee.getDisplayName(), savedTask.getTitle());
                        }
                        taskRepository.save(savedTask);
                    }
                }
            }
        }

        log.info("Bulk update completed for project ID: {}", id);
        
        // Return updated project with details
        return getProjectWithDetails(id);
    }
}
