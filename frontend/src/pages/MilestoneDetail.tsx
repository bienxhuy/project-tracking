import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Calendar, CheckCircle2, Clock, Plus, Edit, Lock, Unlock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { MilestoneProgressBar } from "@/components/MilestoneProgressBar";
import { TaskCard } from "@/components/TaskCard";
import { MilestoneDetail } from "@/types/milestone.type";
import { fetchTempMilestoneDetail } from "@/services/milestone.service";

export const MilestoneDetailPage = () => {
  const { projectId, milestoneId } = useParams<{ projectId: string; milestoneId: string }>();
  const pId = Number(projectId);
  const mId = Number(milestoneId);
  const navigate = useNavigate();

  // TODO: replace with actual auth
  const [userRole] = useState<"student" | "instructor">("student");
  const [isMilestoneLocked, setIsMilestoneLocked] = useState(false);
  const [milestone, setMilestone] = useState<MilestoneDetail | null>(null);

  // Fetch milestone detail on mount
  useEffect(() => {
    // TODO: replace with actual API call
    const data = fetchTempMilestoneDetail();
    setMilestone(data);
    setIsMilestoneLocked(data.isLocked);
  }, [])

  // Handle case where milestone is not found
  if (!milestone) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-extrabold text-foreground mb-4">Không tìm thấy cột mốc</h1>
          <Button onClick={() => navigate("/student/dashboard")}>Quay lại Dashboard</Button>
        </div>
      </div>
    );
  }

  // Calculate progress and task stats of milestone
  const completedTasks = milestone?.tasks.filter(t => t.status === "COMPLETED").length;
  const inProgressTasks = milestone?.tasks.filter(t => t.status === "IN_PROGRESS").length;
  const milestoneProgress = milestone.tasks.length > 0 ? Math.round((completedTasks / milestone.tasks.length) * 100) : 0;

  // TODO: Replace with logic
  const handleTaskToggle = (taskId: number) => {
    console.log("Toggle task:", taskId);
  };

  // TODO: Replace with logic
  const handleToggleMilestoneLock = () => {
    setIsMilestoneLocked(!isMilestoneLocked);
    // In real app, update backend
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-primary/5">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-6">
          <div className="flex items-center justify-between mb-4">
            <Button
              variant="ghost"
              onClick={() => navigate(`/project/${projectId}`)}
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              Quay lại
            </Button>
          </div>

          <div className="flex items-start justify-between">
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <h1 className="text-3xl font-bold text-foreground">{milestone.title}</h1>
                {isMilestoneLocked && (
                  <Badge variant="outline" className="bg-destructive/10 text-destructive border-destructive/20">
                    <Lock className="w-3 h-3 mr-1" />
                    Đã khóa
                  </Badge>
                )}
              </div>
              <p className="text-muted-foreground max-w-3xl">{milestone.description}</p>
            </div>
            <div className="flex gap-2">
              {userRole === "student" && !isMilestoneLocked && (
                <Button size="sm" variant="outline" onClick={() => console.log("Edit milestone")}>
                  <Edit className="w-4 h-4 mr-2" />
                  Chỉnh sửa cột mốc
                </Button>
              )}
              {userRole === "instructor" && (
                <Button size="sm" variant="outline" onClick={handleToggleMilestoneLock}>
                  {isMilestoneLocked ? <Unlock className="w-4 h-4 mr-2" /> : <Lock className="w-4 h-4 mr-2" />}
                  {isMilestoneLocked ? "Mở khóa cột mốc" : "Khóa cột mốc"}
                </Button>
              )}
            </div>
          </div>

          {isMilestoneLocked && (
            <p className="text-sm text-destructive mt-2">Đã khóa</p>
          )}

          <div className="flex items-center gap-6 text-sm text-muted-foreground mt-4">
            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4" />
              <span>
                {milestone.startDate.toLocaleDateString()} - {milestone.endDate.toLocaleDateString()}
              </span>
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-success" />
              <span>{completedTasks}/{milestone.tasks.length} Công việc</span>
            </div>
          </div>
        </div>

        {/* Overall Progress Card */}
        <Card className="mb-8">
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span>Tiến độ</span>
              <span className="text-2xl font-bold text-primary">{milestoneProgress}%</span>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <MilestoneProgressBar tasks={milestone.tasks} />
            <div className="flex items-center justify-between mt-4 text-sm text-muted-foreground">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-success" />
                <span>{completedTasks} Hoàn thành</span>
              </div>
              <div className="flex items-center gap-2">
                <Clock className="w-4 h-4 text-warning" />
                <span>{inProgressTasks} Đang tiến hành</span>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Tasks List */}
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-foreground">Công việc</h2>
            {userRole === "student" && !isMilestoneLocked && (
              <Button className="cursor-pointer" onClick={() => console.log("Create task")}>
                <Plus className="w-4 h-4 mr-2" />
                Tạo công việc
              </Button>
            )}
          </div>
          <div className="space-y-3">
            {milestone.tasks.map((task) => (
              <TaskCard
                key={task.id}
                id={task.id}
                projectId={pId}
                milestoneId={mId}
                title={task.title}
                assignees={task.assignees}
                startDate={task.startDate}
                endDate={task.endDate}
                completed={task.status === "COMPLETED"}
                isLocked={task.isLocked}
                onToggle={() => handleTaskToggle(task.id)}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
