package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.DashboardStatsDTO;

/**
 * Dashboard Service Interface
 * Provides dashboard statistics and overview data
 */
public interface IDashboardService {
    
    /**
     * Get dashboard statistics
     * This method is cached to improve performance
     * 
     * @return Dashboard statistics DTO
     */
    DashboardStatsDTO getDashboardStats();
    
    /**
     * Get dashboard statistics for a specific user
     * 
     * @param userId User ID
     * @return Dashboard statistics DTO
     */
    DashboardStatsDTO getDashboardStatsByUser(Long userId);
    
    /**
     * Refresh dashboard cache
     * Call this when data changes that affect dashboard stats
     */
    void refreshDashboardCache();
    
    /**
     * Refresh dashboard cache for a specific user
     * 
     * @param userId User ID
     */
    void refreshDashboardCacheByUser(Long userId);
}
