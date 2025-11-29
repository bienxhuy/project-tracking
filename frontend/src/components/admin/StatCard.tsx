import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { LucideIcon } from "lucide-react";

type StatCardProps = {
  title: string;
  icon: LucideIcon;
  value: number | string | null;
  helper?: string;
};

export function StatCard({ title, icon: Icon, value, helper }: StatCardProps) {
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

