import { ProjectApiSummary } from "@/types/project.type";
import { ActivityLogEntry } from "@/types/activity.type";

export type ProjectStats = {
  totalProjects: number;
  activeProjects: number;
  completedProjects: number;
  byFaculty: Record<string, number>;
  byYear: Record<string, number>;
};

export type StatusBreakdownEntry = { label: string; value: number };
export type DistributionEntry = { label: string; value: number };

export function calculateProjectStats(projects: ProjectApiSummary[]): ProjectStats {
  if (!projects.length) {
    return {
      totalProjects: 0,
      activeProjects: 0,
      completedProjects: 0,
      byFaculty: {},
      byYear: {},
    };
  }

  return projects.reduce<ProjectStats>(
    (acc, project) => {
      acc.totalProjects += 1;
      const status = project.status?.toUpperCase?.() ?? "";

      if (status === "ACTIVE") acc.activeProjects += 1;
      if (status === "COMPLETED") acc.completedProjects += 1;

      const facultyKey = project.faculty || project.falculty || "Unknown";
      acc.byFaculty[facultyKey] = (acc.byFaculty[facultyKey] || 0) + 1;

      const yearKey = project.year?.toString() ?? "Unknown";
      acc.byYear[yearKey] = (acc.byYear[yearKey] || 0) + 1;

      return acc;
    },
    {
      totalProjects: 0,
      activeProjects: 0,
      completedProjects: 0,
      byFaculty: {},
      byYear: {},
    }
  );
}

export function calculateRecentActivityCount(activityLog: ActivityLogEntry[]): number {
  if (!activityLog.length) return 0;
  const sevenDaysAgo = new Date();
  sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

  return activityLog.filter(
    (entry) => new Date(entry.timestamp).getTime() >= sevenDaysAgo.getTime()
  ).length;
}

export function buildStatusBreakdown(projectStats: ProjectStats | null): StatusBreakdownEntry[] {
  if (!projectStats) return [];
  return [
    { label: "Active", value: projectStats.activeProjects },
    { label: "Completed", value: projectStats.completedProjects },
  ];
}

export function buildDistributionEntries(
  distributionMap: Record<string, number> | undefined
): DistributionEntry[] {
  if (!distributionMap) return [];
  return Object.entries(distributionMap).map(([label, value]) => ({
    label,
    value,
  }));
}

