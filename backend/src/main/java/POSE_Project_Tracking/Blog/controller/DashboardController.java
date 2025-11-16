package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.DashboardStatsDTO;
import POSE_Project_Tracking.Blog.service.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Dashboard Controller
 * Provides endpoints for dashboard statistics (with Redis caching)
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics API")
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Returns cached dashboard statistics")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "Get user-specific dashboard statistics")
    public ResponseEntity<DashboardStatsDTO> getDashboardStatsByUser(@PathVariable Long userId) {
        DashboardStatsDTO stats = dashboardService.getDashboardStatsByUser(userId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/cache/refresh")
    @Operation(summary = "Refresh dashboard cache", description = "Manually refresh the dashboard cache")
    public ResponseEntity<String> refreshDashboardCache() {
        dashboardService.refreshDashboardCache();
        return ResponseEntity.ok("Dashboard cache refreshed successfully");
    }

    @PostMapping("/cache/refresh/user/{userId}")
    @Operation(summary = "Refresh user-specific dashboard cache")
    public ResponseEntity<String> refreshUserDashboardCache(@PathVariable Long userId) {
        dashboardService.refreshDashboardCacheByUser(userId);
        return ResponseEntity.ok("User dashboard cache refreshed successfully");
    }
}
