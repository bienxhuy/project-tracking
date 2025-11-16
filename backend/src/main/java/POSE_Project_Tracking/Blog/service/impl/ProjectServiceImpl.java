package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.config.CacheConfig;
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
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private ProjectMapper projectMapper;

    @Autowired
    private SecurityUtil securityUtil;

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
        Project project = projectRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // Force lazy loading
        project.getMilestones().size();
        project.getTasks().size();

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
