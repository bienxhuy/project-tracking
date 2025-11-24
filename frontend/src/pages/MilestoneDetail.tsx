import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Calendar, CheckCircle2, Clock, Plus, Edit, Lock, Unlock, Trash } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { MilestoneProgressBar } from "@/components/MilestoneProgressBar";
import { TaskCard } from "@/components/TaskCard";
import { MilestoneDetail } from "@/types/milestone.type";
import { fetchTempMilestoneDetail } from "@/services/milestone.service";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";

export const MilestoneDetailPage = () => {
  const { projectId, milestoneId } = useParams<{ projectId: string; milestoneId: string }>();
  const pId = Number(projectId);
  const mId = Number(milestoneId);
  const navigate = useNavigate();

  // TODO: replace with actual auth
  const [userRole] = useState<"student" | "instructor">("student");
  const [isMilestoneLocked, setIsMilestoneLocked] = useState<boolean>(false);
  const [milestone, setMilestone] = useState<MilestoneDetail | null>(null);

  // State for editing milestone title/description
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [title, setTitle] = useState<string>("");
  const [description, setDescription] = useState<string>("");
  const [startDate, setStartDate] = useState<string>(new Date().toISOString().split("T")[0]);
  const [endDate, setEndDate] = useState<string>(new Date().toISOString().split("T")[0]);

  // Fetch milestone detail on mount
  useEffect(() => {
    // TODO: replace with actual API call
    const data = fetchTempMilestoneDetail();
    setMilestone(data);
    setIsMilestoneLocked(data.isLocked);
    setTitle(data.title);
    setDescription(data.description);
    setStartDate(data.startDate.toISOString().split("T")[0]);
    setEndDate(data.endDate.toISOString().split("T")[0]);
  }, [mId]);

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
  const completedTasks = milestone.tasks.filter(t => t.status === "COMPLETED").length;
  const inProgressTasks = milestone.tasks.filter(t => t.status === "IN_PROGRESS").length;
  const milestoneProgress = milestone.tasks.length > 0 ? Math.round((completedTasks / milestone.tasks.length) * 100) : 0;

  // TODO: Replace with logic
  // Event handler for save milestone edits
  const handleSaveEdits = () => {
    setIsEditing(false);
    setMilestone({ ...milestone, title, description, });
  };

  // TODO: Replace with logic
  // Event handler for toggling task completion
  const handleTaskToggle = (taskId: number) => {
    console.log("Toggle task:", taskId);
  };

  // TODO: Replace with logic
  // Event handler for locking/unlocking milestone
  const handleToggleMilestoneLock = () => {
    setIsMilestoneLocked(!isMilestoneLocked);
    // In real app, update backend
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-primary/5">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-6">
          {/* Functional buttons*/}
          <div className="flex items-center justify-between">
            <Button variant="ghost" onClick={() => navigate(`/project/${projectId}`)}>
              <ArrowLeft className="w-4 h-4 mr-2" />
              Quay lại
            </Button>

            {/* Edit and delete milestone button for students / Lock button for instructor */}
            <div className="flex items-center gap-2 mb-4">
              {userRole === "student" && !isMilestoneLocked && (
                <>
                  {!isEditing ? (
                    <>
                      <Button size="sm" variant="ghost" className="cursor-pointer" onClick={() => setIsEditing(true)}>
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button size="sm" variant="destructive" className="cursor-pointer">
                        <Trash className="w-4 h-4" />
                      </Button>
                    </>
                  ) : (
                    <>
                      <Button size="sm" onClick={handleSaveEdits}>Lưu</Button>
                      <Button size="sm" variant="outline" onClick={() => setIsEditing(false)}>Hủy</Button>
                    </>
                  )}
                </>
              )}

              {userRole === "instructor" && (
                <Button size="sm" variant="outline" onClick={handleToggleMilestoneLock}>
                  {isMilestoneLocked ? <Unlock className="w-4 h-4 mr-2" /> : <Lock className="w-4 h-4 mr-2" />}
                  {isMilestoneLocked ? "Mở khóa cột mốc" : "Khóa cột mốc"}
                </Button>
              )}
            </div>
          </div>

          {/* Title + Description (Full Width) */}
          <div className="mb-3">
            <div className="flex items-center gap-3 mb-2">
              {isEditing && !isMilestoneLocked ? (
                <Input
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  autoFocus
                  className="text-4xl font-bold text-foreground h-16 py-3 leading-tight"
                />

              ) : (
                <h1 className="text-3xl font-bold text-foreground">{milestone.title}</h1>
              )}

              {isMilestoneLocked && (
                <Badge
                  variant="outline"
                  className="bg-destructive/10 text-destructive border-destructive/20"
                >
                  <Lock className="w-3 h-3 mr-1" />
                  Đã khóa
                </Badge>
              )}
            </div>

            {isEditing && !isMilestoneLocked ? (
              <Textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                className="text-muted-foreground w-full text-base leading-relaxed"
              />
            ) : (
              <p className="text-muted-foreground w-full text-base leading-relaxed max-w-3xl">
                {milestone.description}
              </p>
            )}
          </div>

          {/* Date Range + Completion */}
          <div className="flex items-center gap-6 text-sm text-muted-foreground">
            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4" />

              {isEditing && !isMilestoneLocked ? (
                <div className="flex gap-2">
                  <Input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    className="h-6 w-32 text-xs"
                  />
                  <span>-</span>
                  <Input
                    type="date"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    className="h-6 w-32 text-xs"
                  />
                </div>
              ) : (
                <span>
                  {milestone.startDate.toLocaleDateString()} - {milestone.endDate.toLocaleDateString()}
                </span>
              )}
            </div>

            {!isEditing && (
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-success" />
                <span>{completedTasks}/{milestone.tasks.length} Công việc</span>
              </div>
            )}
          </div>
        </div>


        {/* Overall Progress Card */}
        <Card className="mb-8">
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span className="font-bold">Tiến độ</span>
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
