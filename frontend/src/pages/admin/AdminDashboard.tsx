import { useEffect, useMemo, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Activity, BarChart3, Briefcase, PieChart, Users } from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { userService } from "@/services/user.service";
import { projectService } from "@/services/project.service";
import { UserStats } from "@/types/user.type";
import { ProjectApiSummary } from "@/types/project.type";
import { useToast } from "@/components/ui/toast";

type ProjectStats = {
  totalProjects: number;
  activeProjects: number;
  completedProjects: number;
  byFaculty: Record<string, number>;
  byYear: Record<string, number>;
};

type ActivityLogEntry = {
  id: number;
  projectId: number;
  projectName: string;
  type: "Progress Report" | "Milestone Update" | "New Project";
  timestamp: string;
  owner: string;
};

const MOCK_ACTIVITY_LOG: ActivityLogEntry[] = [
  {
    id: 1,
    projectId: 1,
    projectName: "GreenCampus Energy Monitor",
    type: "Progress Report",
    timestamp: new Date().toISOString(),
    owner: "Dr. Alvarez",
  },
  {
    id: 2,
    projectId: 2,
    projectName: "DormMate — Roommate Matching",
    type: "Milestone Update",
    timestamp: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
    owner: "Prof. Tan",
  },
  {
    id: 3,
    projectId: 3,
    projectName: "Lab Scheduler & Resource Booker",
    type: "Progress Report",
    timestamp: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    owner: "Ms. Rivera",
  },
  {
    id: 4,
    projectId: 4,
    projectName: "VR Campus Tour",
    type: "New Project",
    timestamp: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000).toISOString(),
    owner: "Dr. Singh",
  },
];

const fetchRecentActivity = async (): Promise<ActivityLogEntry[]> => {
  // TODO: Replace with real API call
  return MOCK_ACTIVITY_LOG;
};

export function AdminDashboard() {
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
          fetchRecentActivity(),
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

  const facultyDistribution = useMemo(() => {
    if (!projectStats) return [];
    return Object.entries(projectStats.byFaculty).map(([label, value]) => ({
      label,
      value,
    }));
  }, [projectStats]);

  const yearDistribution = useMemo(() => {
    if (!projectStats) return [];
    return Object.entries(projectStats.byYear).map(([label, value]) => ({
      label,
      value,
    }));
  }, [projectStats]);

  const statusBreakdown = useMemo(() => {
    if (!projectStats) return [];
    return [
      { label: "Active", value: projectStats.activeProjects },
      { label: "Completed", value: projectStats.completedProjects },
    ];
  }, [projectStats]);

  const overviewStats = [
    {
      title: "Total Users",
      icon: Users,
      value: userStats?.totalUsers ?? null,
      helper: userStats
        ? `${userStats.totalAdmins} Admin · ${userStats.totalInstructors} Instr · ${userStats.totalStudents} Stud`
        : "Awaiting data",
    },
    {
      title: "Projects in System",
      icon: Briefcase,
      value: projectStats?.totalProjects ?? null,
      helper: projectStats ? `${projectStats.activeProjects} active right now` : "Awaiting data",
    },
    {
      title: "Completion Snapshot",
      icon: PieChart,
      value:
        projectStats && projectStats.totalProjects
          ? `${Math.round(
              (projectStats.completedProjects / projectStats.totalProjects) * 100
            )}%`
          : null,
      helper:
        projectStats && projectStats.totalProjects
          ? `${projectStats.completedProjects} of ${projectStats.totalProjects} completed`
          : "Awaiting data",
    },
    {
      title: "Recent Updates (7d)",
      icon: Activity,
      value: recentActivityCount,
      helper: "Progress reports + milestones",
    },
  ];

  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-3xl font-bold text-gray-900">System Statistics</h2>
        <p className="mt-2 text-sm text-gray-600">
          Live snapshot of platform usage to support monitoring and quick decision-making.
        </p>
      </div>

      {loading ? (
        <DashboardSkeleton />
      ) : error ? (
        <Card>
          <CardContent className="py-10 text-center text-sm text-gray-600">
            {error}
          </CardContent>
        </Card>
      ) : !userStats && !projectStats ? (
        <Card>
          <CardContent className="py-10 text-center text-sm text-gray-600">
            No data available.
          </CardContent>
        </Card>
      ) : (
        <>
          <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            {overviewStats.map((stat) => (
              <StatCard
                key={stat.title}
                title={stat.title}
                icon={stat.icon}
                value={stat.value}
                helper={stat.helper}
              />
            ))}
          </section>

          <section className="grid gap-6 lg:grid-cols-3">
            <Card className="lg:col-span-2">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base font-semibold">
                  <BarChart3 className="h-4 w-4 text-blue-600" />
                  Projects by Status
                </CardTitle>
              </CardHeader>
              <CardContent>
                {statusBreakdown.length ? (
                  <div className="space-y-4">
                    {statusBreakdown.map((item) => (
                      <div key={item.label} className="flex items-center">
                        <div className="flex-1">
                          <p className="text-sm font-medium text-gray-800">{item.label}</p>
                          <div className="mt-2 h-2 rounded-full bg-gray-100">
                            <div
                              className="h-2 rounded-full bg-blue-500"
                              style={{
                                width: projectStats?.totalProjects
                                  ? `${(item.value / projectStats.totalProjects) * 100}%`
                                  : 0,
                              }}
                            />
                          </div>
                        </div>
                        <span className="ml-4 text-sm font-semibold text-gray-900">
                          {item.value || "No data available"}
                        </span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-gray-500">No data available.</p>
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base font-semibold">
                  <Users className="h-4 w-4 text-green-600" />
                  User Composition
                </CardTitle>
              </CardHeader>
              <CardContent>
                {userStats ? (
                  <div className="space-y-3 text-sm">
                    <UserRoleRow label="Admins" value={userStats.totalAdmins} />
                    <UserRoleRow label="Instructors" value={userStats.totalInstructors} />
                    <UserRoleRow label="Students" value={userStats.totalStudents} />
                    <UserRoleRow label="Inactive Accounts" value={userStats.totalInactive} />
                  </div>
                ) : (
                  <p className="text-sm text-gray-500">No data available.</p>
                )}
              </CardContent>
            </Card>
          </section>

          <section className="grid gap-6 lg:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base font-semibold">
                  <PieChart className="h-4 w-4 text-purple-600" />
                  Projects by Faculty
                </CardTitle>
              </CardHeader>
              <CardContent>
                <DistributionList
                  data={facultyDistribution}
                  emptyMessage="No data available."
                />
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-base font-semibold">
                  <Briefcase className="h-4 w-4 text-amber-600" />
                  Projects by Academic Year
                </CardTitle>
              </CardHeader>
              <CardContent>
                <DistributionList data={yearDistribution} emptyMessage="No data available." />
              </CardContent>
            </Card>
          </section>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-base font-semibold">
                <Activity className="h-4 w-4 text-rose-600" />
                Recent Activity (Last 7 Days)
              </CardTitle>
            </CardHeader>
            <CardContent>
              {recentActivityCount === null || !recentActivity.length ? (
                <p className="text-sm text-gray-500">No data available.</p>
              ) : (
                <>
                  <p className="text-sm text-gray-600">
                    {recentActivityCount} submissions logged in the last 7 days.
                  </p>
                  <div className="mt-4 space-y-4">
                    {recentActivity.slice(0, 5).map((activity) => (
                      <div
                        key={activity.id}
                        className="flex items-center justify-between rounded-lg border border-gray-100 p-3"
                      >
                        <div>
                          <p className="text-sm font-semibold text-gray-900">{activity.projectName}</p>
                          <p className="text-xs text-gray-500">
                            {activity.type} • {activity.owner}
                          </p>
                        </div>
                        <p className="text-xs text-gray-500">
                          {new Intl.DateTimeFormat("en-US", {
                            month: "short",
                            day: "numeric",
                          }).format(new Date(activity.timestamp))}
                        </p>
                      </div>
                    ))}
                  </div>
                </>
              )}
            </CardContent>
          </Card>
        </>
      )}
    </div>
  );
}

function StatCard({
  title,
  icon: Icon,
  value,
  helper,
}: {
  title: string;
  icon: LucideIcon;
  value: number | string | null;
  helper?: string;
}) {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium text-gray-600">{title}</CardTitle>
        <div className="rounded-lg bg-gray-100 p-2">
          <Icon className="h-4 w-4 text-gray-700" />
        </div>
      </CardHeader>
      <CardContent>
        {value !== null ? (
          <div className="text-3xl font-bold text-gray-900">{value}</div>
        ) : (
          <p className="text-sm text-gray-500">No data available.</p>
        )}
        {helper && <p className="text-xs text-gray-500">{helper}</p>}
      </CardContent>
    </Card>
  );
}

function UserRoleRow({ label, value }: { label: string; value: number }) {
  return (
    <div className="flex items-center justify-between">
      <span className="text-gray-600">{label}</span>
      <span className="font-semibold text-gray-900">{value}</span>
    </div>
  );
}

function DistributionList({
  data,
  emptyMessage = "No data available.",
}: {
  data: Array<{ label: string; value: number }>;
  emptyMessage?: string;
}) {
  if (!data.length) {
    return <p className="text-sm text-gray-500">{emptyMessage}</p>;
  }

  const maxValue = Math.max(...data.map((item) => item.value));

  return (
    <div className="space-y-4">
      {data.map((item) => (
        <div key={item.label} className="flex items-center">
          <div className="flex-1">
            <p className="text-sm font-medium text-gray-800">{item.label}</p>
            <div className="mt-1 h-2 rounded-full bg-gray-100">
              <div
                className="h-2 rounded-full bg-indigo-500"
                style={{
                  width: maxValue ? `${(item.value / maxValue) * 100}%` : 0,
                }}
              />
            </div>
          </div>
          <span className="ml-4 text-sm font-semibold text-gray-900">
            {item.value || "No data available"}
          </span>
        </div>
      ))}
    </div>
  );
}

function DashboardSkeleton() {
  return (
    <div className="space-y-6">
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {[...Array(4)].map((_, index) => (
          <Card key={index} className="animate-pulse">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <div className="h-4 w-24 rounded bg-gray-200" />
              <div className="h-8 w-8 rounded-lg bg-gray-200" />
            </CardHeader>
            <CardContent>
              <div className="h-8 w-16 rounded bg-gray-200" />
              <div className="mt-2 h-3 w-32 rounded bg-gray-100" />
            </CardContent>
          </Card>
        ))}
      </div>
      <Card className="animate-pulse">
        <CardContent className="h-48" />
      </Card>
      <div className="grid gap-6 lg:grid-cols-2">
        <Card className="animate-pulse">
          <CardContent className="h-64" />
        </Card>
        <Card className="animate-pulse">
          <CardContent className="h-64" />
        </Card>
      </div>
    </div>
  );
}

function calculateProjectStats(projects: ProjectApiSummary[]): ProjectStats {
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

      const facultyKey = project.faculty || "Unknown";
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

function calculateRecentActivityCount(activityLog: ActivityLogEntry[]): number {
  if (!activityLog.length) return 0;
  const sevenDaysAgo = new Date();
  sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

  return activityLog.filter(
    (entry) => new Date(entry.timestamp).getTime() >= sevenDaysAgo.getTime()
  ).length;
}