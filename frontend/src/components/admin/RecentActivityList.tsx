import { Activity } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ActivityLogEntry } from "@/types/activity.type";

type RecentActivityListProps = {
  recentActivity: ActivityLogEntry[];
  recentActivityCount: number | null;
};

export function RecentActivityList({
  recentActivity,
  recentActivityCount,
}: RecentActivityListProps) {
  return (
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
  );
}

