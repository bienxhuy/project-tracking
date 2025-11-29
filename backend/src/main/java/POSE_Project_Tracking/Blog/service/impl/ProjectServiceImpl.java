package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.config.CacheConfig;
import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.ProjectMapper;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.ProjectMemberRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IMilestoneService;
import POSE_Project_Tracking.Blog.service.IProjectService;
import POSE_Project_Tracking.Blog.service.ITaskService;
import POSE_Project_Tracking.Blog.util.AcademicYearUtil;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.DASHBOARD_STATS_CACHE, allEntries = true)
    })
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
        }

        return projectMapper.toResponse(project);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.DASHBOARD_STATS_CACHE, allEntries = true)
    })
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
            // Clear all existing members (orphanRemoval = true will delete them from DB)
            if (project.getMembers() != null) {
                project.getMembers().clear();
            }

            // Save to trigger orphan removal
            project = projectRepository.save(project);

            // Add new members
            if (!projectReq.getStudentIds().isEmpty()) {
                addProjectMembers(project, projectReq.getStudentIds());
            }
        }

        project = projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    @Override
    @Cacheable(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id")
    public ProjectRes getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        return projectMapper.toResponse(project);
    }

    @Override
    @Cacheable(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "'detailed_' + #id")
    public ProjectRes getProjectWithDetails(Long id) {
        Project project = projectRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Force lazy loading of collections separately to avoid MultipleBagFetchException
        // Hibernate cannot fetch multiple bags in one query
        if (project.getMilestones() != null) {
            project.getMilestones().size();
        }
        if (project.getMembers() != null) {
            project.getMembers().size();
        }
        
        return projectMapper.toResponse(project);
    }

    @Override
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, key = "'all'")
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
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true),
            @CacheEvict(value = CacheConfig.DASHBOARD_STATS_CACHE, allEntries = true)
    })
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        if (Boolean.TRUE.equals(project.getLocked())) {
            throw new CustomException(PROJECT_LOCKED);
        }

        projectRepository.delete(project);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true)
    })
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
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true)
    })
    public void unlockProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        project.setLocked(false);
        project.setLockedBy(null);
        project.setLockedAt(null);

        projectRepository.save(project);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true)
    })
    public void lockProjectContent(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Get current authenticated user
        User currentUser = securityUtil.getCurrentUser();

        project.setIsObjDesLocked(true);
        project.setLockedBy(currentUser);
        project.setLockedAt(LocalDateTime.now());

        projectRepository.save(project);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true)
    })
    public void unlockProjectContent(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        project.setIsObjDesLocked(false);

        projectRepository.save(project);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PROJECT_DETAIL_CACHE, key = "#id"),
            @CacheEvict(value = CacheConfig.PROJECT_LIST_CACHE, allEntries = true)
    })
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
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, 
               key = "'my_projects_' + #root.target.securityUtil.getCurrentUser().id + '_' + #year + '_' + #semester + '_' + #batch")
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
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, 
               key = "'my_projects_' + #root.target.securityUtil.getCurrentUser().id + '_' + #status + '_' + #year + '_' + #semester + '_' + #batch")
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
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, 
               key = "'all_projects_' + #year + '_' + #semester + '_' + #batch")
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
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, key = "'all_projects_student_' + #studentId")
    public List<ProjectRes> getAllProjectsByStudent(Long studentId) {
        return projectRepository.findProjectsByMemberUserId(studentId).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, key = "'all_projects_student_' + #studentId + '_' + #status")
    public List<ProjectRes> getAllProjectsByStudentAndStatus(Long studentId, EProjectStatus status) {
        return projectRepository.findProjectsByMemberUserIdAndStatus(studentId, status).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, key = "'all_my_projects_' + #root.target.securityUtil.getCurrentUser().id")
    public List<ProjectRes> getAllMyProjects() {
        User currentUser = securityUtil.getCurrentUser();
        return projectRepository.findProjectsByMemberUserId(currentUser.getId()).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.PROJECT_LIST_CACHE, key = "'all_my_projects_' + #root.target.securityUtil.getCurrentUser().id + '_' + #status")
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
}
