import { useState } from "react";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { Plus } from "lucide-react";

import { ProgressReportCard } from "@/components/ProgressReportCard";
import { ProgressReportEditor } from "@/components/ProgressReportEditor";
import { Report } from "@/types/report.type";


interface ProgressReportThreadProps {
  taskId: number; // Required for creating reports
  reports: Report[];
  projectMembers: Array<{ id: number; name: string; initials: string }>;
  userRole: "student" | "instructor";
  isTaskLocked: boolean;
  onReportCreated?: (newReport: Report) => void; // Pass new report to parent
  onReportUpdated?: (updatedReport: Report) => void; // Pass updated report to parent
}


export const ProgressReportThread = ({
  taskId,
  reports,
  projectMembers,
  userRole,
  isTaskLocked,
  onReportCreated,
  onReportUpdated,
}: ProgressReportThreadProps) => {
  const [showNewReportForm, setShowNewReportForm] = useState(false);

  const handleCreateSuccess = (newReport: Report) => {
    setShowNewReportForm(false);
    onReportCreated?.(newReport);
  };

  // Handlers for report updates to notify parent component
  const handleReportUpdated = (updatedReport: Report) => {
    onReportUpdated?.(updatedReport);
  };

  // Cancel new report creation callback to hide the form
  const handleCancelNewReport = () => {
    setShowNewReportForm(false);
  };

  const canCreateReport = userRole === "student" && !isTaskLocked;

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle>Báo cáo tiến độ</CardTitle>
          {canCreateReport && !showNewReportForm && (
            <Button size="sm" onClick={() => setShowNewReportForm(true)}>
              <Plus className="w-4 h-4 mr-2" />
              Tạo báo cáo
            </Button>
          )}
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* New Report Form */}
        {showNewReportForm && (
          <>
            <Card className="border-primary/50 bg-primary/5">
              <CardContent className="pt-6">
                <ProgressReportEditor
                  mode="create"
                  taskId={taskId}
                  onSuccess={handleCreateSuccess}
                  onCancel={handleCancelNewReport}
                />
              </CardContent>
            </Card>
            <Separator />
          </>
        )}

        {/* Existing Reports */}
        {reports.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            <p>Chưa có báo cáo nào.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {reports.map((report) => (
              <ProgressReportCard
                key={report.id}
                report={report}
                projectMembers={projectMembers}
                userRole={userRole}
                isTaskLocked={isTaskLocked}
                onReportUpdated={handleReportUpdated}
              />
            ))}
          </div>
        )}

        {isTaskLocked && (
          <p className="text-sm text-destructive text-center">
            Công việc đã bị khóa. Không thể thêm báo cáo mới.
          </p>
        )}
      </CardContent>
    </Card>
  );
};
