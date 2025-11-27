import { useEffect, useMemo, useState } from "react";
import { userService } from "@/services/user.service";
import { projectService } from "@/services/project.service";
import { activityService } from "@/services/activity.service";
import { useToast } from "@/components/ui/toast";
import { UserStats } from "@/types/user.type";
import { ActivityLogEntry } from "@/types/activity.type";
import {
  DistributionEntry,
  ProjectStats,
  StatusBreakdownEntry,
  buildDistributionEntries,
  buildStatusBreakdown,
  calculateProjectStats,
  calculateRecentActivityCount,
} from "@/utils/dashboard.utils";

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
      try {
        setLoading(true);
        setError(null);

        const [usersData, projectsData, activityLog] = await Promise.all([
          userService.getUsers(),
          projectService.getProjects(),
          activityService.getRecentActivity(),
        ]);

        const calculatedUserStats = userService.calculateStats(usersData);
        const calculatedProjectStats = calculateProjectStats(projectsData);
        const activityCount = calculateRecentActivityCount(activityLog);

        setUserStats(calculatedUserStats);
        setProjectStats(calculatedProjectStats);
        setRecentActivity(activityLog);
        setRecentActivityCount(activityCount);
      } catch (err) {
        console.error("Failed to load dashboard data:", err);
        setError("Unable to retrieve system statistics right now.");
        addToast({
          title: "Error",
          description: "Unable to retrieve system statistics right now.",
          variant: "destructive",
        });
      } finally {
        setLoading(false);
      }
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

