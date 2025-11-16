package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.config.CacheConfig;
import POSE_Project_Tracking.Blog.dto.DashboardStatsDTO;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

/**
 * Dashboard Service Implementation
 * Provides cached dashboard statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements IDashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;

    /**
     * Get dashboard statistics with caching
     * Cache is automatically managed by Spring Cache
     */
    @Override
    @Cacheable(value = CacheConfig.DASHBOARD_STATS_CACHE, key = "'global'")
    public DashboardStatsDTO getDashboardStats() {
        log.info("Calculating dashboard statistics (not from cache)");
        
        // Get all projects
        List<Project> allProjects = projectRepository.findAll();
        
        // Calculate project statistics
        long totalProjects = allProjects.size();
        long activeProjects = allProjects.stream()
                .filter(p -> p.getStatus() == EProjectStatus.ACTIVE)
                .count();
        long completedProjects = allProjects.stream()
                .filter(p -> p.getStatus() == EProjectStatus.COMPLETED)
                .count();
        long lockedProjects = allProjects.stream()
                .filter(p -> p.getStatus() == EProjectStatus.LOCKED)
                .count();

        // Get all tasks
        List<Task> allTasks = taskRepository.findAll();
        
        // Calculate task statistics
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == ETaskStatus.COMPLETED)
                .count();
        long inProgressTasks = allTasks.stream()
                .filter(t -> t.getStatus() == ETaskStatus.IN_PROGRESS)
                .count();
        
        // Calculate overdue tasks
        LocalDateTime now = LocalDateTime.now();
        long overdueTasks = allTasks.stream()
                .filter(t -> t.getEndDate() != null && t.getEndDate().isBefore(ChronoLocalDate.from(now))
                        && t.getStatus() != ETaskStatus.COMPLETED)
                .count();

        // Calculate today's and this week's tasks
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekEnd = now.plusDays(7);
        
        long todayTasks = allTasks.stream()
                .filter(t -> t.getEndDate() != null 
                        && !t.getEndDate().isBefore(ChronoLocalDate.from(todayStart))
                        && t.getEndDate().isBefore(ChronoLocalDate.from(todayStart.plusDays(1))))
                .count();
        
        long thisWeekTasks = allTasks.stream()
                .filter(t -> t.getEndDate() != null 
                        && !t.getEndDate().isBefore(ChronoLocalDate.from(now))
                        && t.getEndDate().isBefore(ChronoLocalDate.from(weekEnd)))
                .count();

        // Get all milestones
        List<Milestone> allMilestones = milestoneRepository.findAll();
        
        long totalMilestones = allMilestones.size();
        long completedMilestones = allMilestones.stream()
                .filter(m -> m.getStatus() == EMilestoneStatus.COMPLETED)
                .count();

        // Calculate overall progress
        double overallProgress = totalTasks > 0 
                ? (double) completedTasks / totalTasks * 100 
                : 0.0;

        // Get total members count
        int totalMembers = (int) userRepository.count();

        return DashboardStatsDTO.builder()
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .completedProjects(completedProjects)
                .lockedProjects(lockedProjects)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .overdueTasks(overdueTasks)
                .totalMilestones(totalMilestones)
                .completedMilestones(completedMilestones)
                .overallProgress(overallProgress)
                .totalMembers(totalMembers)
                .todayTasks(todayTasks)
                .thisWeekTasks(thisWeekTasks)
                .upcomingDeadlines(thisWeekTasks)
                .build();
    }

    /**
     * Get dashboard statistics for a specific user
     */
    @Override
    @Cacheable(value = CacheConfig.DASHBOARD_STATS_CACHE, key = "'user_' + #userId")
    public DashboardStatsDTO getDashboardStatsByUser(Long userId) {
        log.info("Calculating dashboard statistics for user {} (not from cache)", userId);
        
        // TODO: Implement user-specific statistics
        // This is a placeholder - adjust based on your actual user-project relationship
        
        return getDashboardStats(); // For now, return global stats
    }

    /**
     * Clear dashboard cache
     * Called when data changes that affect dashboard
     */
    @Override
    @CacheEvict(value = CacheConfig.DASHBOARD_STATS_CACHE, key = "'global'")
    public void refreshDashboardCache() {
        log.info("Dashboard cache cleared");
    }

    /**
     * Clear dashboard cache for specific user
     */
    @Override
    @CacheEvict(value = CacheConfig.DASHBOARD_STATS_CACHE, key = "'user_' + #userId")
    public void refreshDashboardCacheByUser(Long userId) {
        log.info("Dashboard cache cleared for user {}", userId);
    }
}
