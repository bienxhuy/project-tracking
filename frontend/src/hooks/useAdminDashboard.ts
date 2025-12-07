import { useEffect, useMemo, useState } from "react";
import { userService } from "@/services/user.service";
import { projectService } from "@/services/project.service";
import { activityService } from "@/services/activity.service";
import { useToast } from "@/components/ui/toast";
import { UserStats } from "@/types/user.type";
import { ActivityLogEntry } from "@/types/activity.type";
import { Project } from "@/types/project.type";
import { ApiResponse } from "@/types/auth.type";
import apiClient from "@/api/axios.customize";
import {
  DistributionEntry,
  ProjectStats,
  StatusBreakdownEntry,
  buildDistributionEntries,
  buildStatusBreakdown,
  calculateProjectStats,
  calculateRecentActivityCount,
} from "@/utils/dashboard.utils";

// Helper function to parse project dates (same as in project.service.ts)
function parseProjectDates<T extends Project>(project: any): T {
  return {
    ...project,
    startDate: new Date(project.startDate),
    endDate: new Date(project.endDate),
    students: project.students?.filter((mem: any) => mem.role === "STUDENT") || [],
    milestones: project.milestones?.map((milestone: any) => ({
      ...milestone,
      startDate: new Date(milestone.startDate),
      endDate: new Date(milestone.endDate),
    })) || [],
  } as T;
}

type AdminDashboardState = {
  userStats: UserStats | null;
  projectStats: ProjectStats | null;
  recentActivity: ActivityLogEntry[];
  recentActivityCount: number | null;
  loading: boolean;
  error: string | null;
  statusBreakdown: StatusBreakdownEntry[];
  facultyDistribution: DistributionEntry[];
  yearDistribution: DistributionEntry[];
};

export function useAdminDashboard(): AdminDashboardState {
  const [userStats, setUserStats] = useState<UserStats | null>(null);
  const [projectStats, setProjectStats] = useState<ProjectStats | null>(null);
  const [recentActivity, setRecentActivity] = useState<ActivityLogEntry[]>([]);
  const [recentActivityCount, setRecentActivityCount] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const { addToast } = useToast();

  useEffect(() => {
    const loadDashboardData = async () => {
      setLoading(true);
      setError(null);

      // Load users data
      try {
        const usersData = await userService.getUsers();
        if (usersData && Array.isArray(usersData)) {
          const calculatedUserStats = userService.calculateStats(usersData);
          setUserStats(calculatedUserStats);
        }
      } catch (err: any) {
        addToast({
          title: "Warning",
          description: "Unable to load user statistics. Some data may be missing.",
          variant: "destructive",
        });
      }

      // Load projects data with fallback strategy
      let projectsData: any = null;
      let projectsLoaded = false;

      // Strategy 1: Try main endpoint
      try {
        projectsData = await projectService.getProjects();
        if (projectsData && projectsData.status === "success" && Array.isArray(projectsData.data)) {
          const mainProjects = projectsData.data.map(p => parseProjectDates<Project>(p));
          const completedCount = mainProjects.filter(p => p.status === "COMPLETED").length;
          
          // If we have projects but no COMPLETED, try to load COMPLETED separately
          if (mainProjects.length > 0 && completedCount === 0) {
            try {
              const completedResponse = await apiClient.get<ApiResponse<Project[]>>(
                '/api/v1/projects/status/COMPLETED'
              );
              if (completedResponse.data.status === "success" && Array.isArray(completedResponse.data.data)) {
                const completedProjects = completedResponse.data.data.map(p => parseProjectDates<Project>(p));
                mainProjects.push(...completedProjects);
              }
            } catch (err: any) {
              // Silently fail
            }
          }
          
          const calculatedProjectStats = calculateProjectStats(mainProjects);
          setProjectStats(calculatedProjectStats);
          projectsLoaded = true;
        }
      } catch (err: any) {
        // Silently fail and try next strategy
      }

      // Strategy 2: Try filter endpoint (may work better as it filters data first)
      if (!projectsLoaded) {
        try {
          projectsData = await projectService.getProjectsWithFilters();
          if (projectsData && projectsData.status === "success" && Array.isArray(projectsData.data)) {
            const filterProjects = projectsData.data.map(p => parseProjectDates<Project>(p));
            const completedCount = filterProjects.filter(p => p.status === "COMPLETED").length;
            
            // If we have projects but no COMPLETED, try to load COMPLETED separately
            if (filterProjects.length > 0 && completedCount === 0) {
              try {
                const completedResponse = await apiClient.get<ApiResponse<Project[]>>(
                  '/api/v1/projects/status/COMPLETED'
                );
                if (completedResponse.data.status === "success" && Array.isArray(completedResponse.data.data)) {
                  const completedProjects = completedResponse.data.data.map(p => parseProjectDates<Project>(p));
                  filterProjects.push(...completedProjects);
                }
              } catch (err: any) {
                // Silently fail
              }
            }
            
            const calculatedProjectStats = calculateProjectStats(filterProjects);
            setProjectStats(calculatedProjectStats);
            projectsLoaded = true;
          }
        } catch (err: any) {
          // Silently fail and try next strategy
        }
      }

      // Strategy 3: Try loading by status separately (smaller datasets)
      // Load each status independently so if one fails, we can still use the other
      if (!projectsLoaded) {
        const allProjects: Project[] = [];
        let anyStatusLoaded = false;

        // Try to load ACTIVE projects
        try {
          const activeResponse = await apiClient.get<ApiResponse<Project[]>>(
            '/api/v1/projects/status/ACTIVE'
          );
          if (activeResponse.data.status === "success" && Array.isArray(activeResponse.data.data)) {
            const activeProjects = activeResponse.data.data.map(p => parseProjectDates<Project>(p));
            allProjects.push(...activeProjects);
            anyStatusLoaded = true;
          }
        } catch (err: any) {
          // Silently fail
        }

        // Try to load COMPLETED projects
        try {
          const completedResponse = await apiClient.get<ApiResponse<Project[]>>(
            '/api/v1/projects/status/COMPLETED'
          );
          if (completedResponse.data.status === "success" && Array.isArray(completedResponse.data.data)) {
            const completedProjects = completedResponse.data.data.map(p => parseProjectDates<Project>(p));
            allProjects.push(...completedProjects);
            anyStatusLoaded = true;
          }
        } catch (err: any) {
          // Silently fail
        }

        // If we got at least some projects, use them
        if (anyStatusLoaded && allProjects.length > 0) {
          const calculatedProjectStats = calculateProjectStats(allProjects);
          setProjectStats(calculatedProjectStats);
          projectsLoaded = true;
        }
      }

      // Strategy 4: Try loading by current year/semester (smaller dataset)
      if (!projectsLoaded) {
        try {
          const currentYear = new Date().getFullYear();
          const currentMonth = new Date().getMonth() + 1;
          // Semester 1: Jan-Jun, Semester 2: Jul-Dec
          const currentSemester = currentMonth <= 6 ? 1 : 2;
          
          projectsData = await projectService.getProjectsWithFilters({
            year: currentYear,
            semester: currentSemester,
          });
          
          if (projectsData && projectsData.status === "success" && Array.isArray(projectsData.data)) {
            const calculatedProjectStats = calculateProjectStats(projectsData.data);
            setProjectStats(calculatedProjectStats);
            projectsLoaded = true;
          }
        } catch (err: any) {
          // Silently fail
        }
      }

      // If all strategies failed
      if (!projectsLoaded) {
        addToast({
          title: "Warning",
          description: "Unable to load project statistics. Some data may be missing.",
          variant: "destructive",
        });
      }

      // Load activity log
      try {
        const activityLog = await activityService.getRecentActivity();
        const validActivityLog = Array.isArray(activityLog) ? activityLog : [];
        const activityCount = calculateRecentActivityCount(validActivityLog);
        setRecentActivity(validActivityLog);
        setRecentActivityCount(activityCount);
      } catch (err: any) {
        // Activity log is optional, so we just use empty array
        setRecentActivity([]);
        setRecentActivityCount(0);
      }

      setLoading(false);
    };

    loadDashboardData();
  }, [addToast]);

  const statusBreakdown = useMemo(
    () => buildStatusBreakdown(projectStats),
    [projectStats]
  );

  const facultyDistribution = useMemo<DistributionEntry[]>(
    () => buildDistributionEntries(projectStats?.byFaculty),
    [projectStats]
  );

  const yearDistribution = useMemo<DistributionEntry[]>(
    () => buildDistributionEntries(projectStats?.byYear),
    [projectStats]
  );

  return {
    userStats,
    projectStats,
    recentActivity,
    recentActivityCount,
    loading,
    error,
    statusBreakdown,
    facultyDistribution,
    yearDistribution,
  };
}

