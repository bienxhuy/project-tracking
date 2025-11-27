import { useMemo } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { BarChart3, Briefcase, PieChart, Users } from "lucide-react";
import { useAdminDashboard } from "@/hooks/useAdminDashboard";
import { StatCard } from "@/components/admin/StatCard";
import { DistributionList } from "@/components/admin/DistributionList";
import { UserRoleRow } from "@/components/admin/UserRoleRow";
import { RecentActivityList } from "@/components/admin/RecentActivityList";
import { DashboardSkeleton } from "@/components/admin/DashboardSkeleton";
import { StatusProgressBar } from "@/components/admin/StatusProgressBar";

export function AdminDashboard() {
  const {
    userStats,
    projectStats,
    recentActivity,
    recentActivityCount,
    loading,
    error,
    statusBreakdown,
    facultyDistribution,
    yearDistribution,
  } = useAdminDashboard();

  const overviewStats = useMemo(
    () => [
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
        icon: BarChart3,
        value: recentActivityCount,
        helper: "Progress reports + milestones",
      },
    ],
    [projectStats, recentActivityCount, userStats]
  );

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
                          <StatusProgressBar
                            value={item.value}
                            total={projectStats?.totalProjects ?? 0}
                          />
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

          <RecentActivityList
            recentActivity={recentActivity}
            recentActivityCount={recentActivityCount}
          />
        </>
      )}
    </div>
  );
}