package POSE_Project_Tracking.Blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dashboard Statistics DTO
 * Contains aggregated statistics for the dashboard view
 * Implements Serializable for Redis caching
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long totalProjects;
    private Long activeProjects;
    private Long completedProjects;
    private Long lockedProjects;
    
    private Long totalTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long overdueTasks;
    
    private Long totalMilestones;
    private Long completedMilestones;
    
    private Double overallProgress;
    private Integer totalMembers;
    
    // Additional stats
    private Long todayTasks;
    private Long thisWeekTasks;
    private Long upcomingDeadlines;
}
