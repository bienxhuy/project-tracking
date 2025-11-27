import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { CheckCircle2, Clock, Lock, Unlock, Plus, Edit } from "lucide-react";
import { fetchDetailProject } from "@/services/project.service";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { Separator } from "@/components/ui/separator";

import { ProjectDetail } from "@/types/project.type";
import { statusConfig } from "@/types/project.type";
import { MilestoneCard } from "@/components/MilestoneCard";
import { ProjectProgressBar } from "@/components/ProjectProgressBar";


export const ProjectDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  // TODO: replace with actual auth
  const [userRole] = useState<"student" | "instructor">("student");

  // Project content state
  const [project, setProject] = useState<ProjectDetail | null>(null);
  const [isContentLocked, setIsContentLocked] = useState<boolean>(false);

  const [objective, setObjective] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [isEditing, setIsEditing] = useState<boolean>(false);

  // State to check if creating new milestone or not
  const [creatingMilestone, setCreatingMilestone] = useState<boolean>(false);

  // State for instructor to add comments
  const [comment, setComment] = useState<string>("");
  const [showCommentSection, setShowCommentSection] = useState<boolean>(false);

  // Fetch project detail on mount
  useEffect(() => {
    const data = fetchDetailProject(Number(id));
    setProject(data);
    setIsContentLocked(data.isLocked);
    setObjective(data.objective);
    setContent(data.content);
  }, [id]);

  // Handle case where project is not found
  if (!project) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-extrabold text-foreground mb-4">Không tìm thấy dự án</h1>
          <Button onClick={() => navigate("/student/dashboard")}>Quay lại Dashboard</Button>
        </div>
      </div>
    );
  }

  // In case project is loaded

  // Derived project states
  const statusInfo = statusConfig[project.status];
  const completedMilestones = project.milestones.filter(m => m.status === "COMPLETED").length;

  // Calculate overall progress based on all tasks across milestones
  const totalTasks = project.milestones.reduce((sum, milestone) => sum + milestone.tasksTotal, 0);
  const totalCompletedTasks = project.milestones.reduce((sum, milestone) => sum + milestone.tasksCompleted, 0);
  const overallProgress = totalTasks > 0 ? Math.round((totalCompletedTasks / totalTasks) * 100) : 0;

  // Event handlers for content editing
  const handleSaveContent = () => {
    setProject({ ...project, objective, content });
    setIsEditing(false);
    // In real app, save to backend
  };

  // Event handler for locking/unlocking content & objective
  const handleToggleLock = () => {
    setIsContentLocked(!isContentLocked);
    // In real app, update backend
  };

  const handleSubmitComment = () => {
    if (!comment.trim()) return;
    // In real app, save comment to backend
    setComment("");
    setShowCommentSection(false);
  };

  return (
    <div className="min-h-screen bg-background">
      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 py-8">
        <h1 className="scroll-m-20 text-left text-4xl font-extrabold tracking-tight text-balance mb-7">
          {project.title}
        </h1>

        {/* Objectives and Description Card */}
        <Card className="mb-8">
          {/* Title and Controls */}
          <CardHeader>
            <CardTitle className="flex flex-col sm:flex-row gap-3 items-center justify-between">
              <span className="font-bold">Mục tiêu & Mô tả</span>
              <div className="flex gap-2">
                {userRole === "instructor" && (
                  <>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={handleToggleLock}
                    >
                      {isContentLocked ? <Unlock className="w-4 h-4 mr-2" /> : <Lock className="w-4 h-4 mr-2" />}
                      {isContentLocked ? "Mở khóa nội dung" : "Khóa nội dung"}
                    </Button>
                  </>
                )}
                {userRole === "student" && !isContentLocked && (
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => setIsEditing(!isEditing)}
                  >
                    <Edit className="w-4 h-4 mr-2" />
                    {isEditing ? "Hủy" : "Chỉnh sửa"}
                  </Button>
                )}
              </div>
            </CardTitle>
          </CardHeader>
          {/* Objective and Content */}
          <CardContent className="space-y-4">
            {/* Objective */}
            <div>
              <h3 className="text-sm font-semibold text-muted-foreground mb-2">Mục tiêu</h3>
              {isEditing && !isContentLocked ? (
                <Textarea
                  value={objective}
                  onChange={(e) => setObjective(e.target.value)}
                  placeholder="Nhập mục tiêu dự án..."
                  className="min-h-[80px]"
                />
              ) : (
                <p className="text-foreground">{project.objective}</p>
              )}
            </div>
            <Separator />
            {/* Content */}
            <div>
              <h3 className="text-sm font-semibold text-muted-foreground mb-2">Mô tả</h3>
              {isEditing && !isContentLocked ? (
                <Textarea
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="Nhập mô tả dự án..."
                  className="min-h-[100px]"
                />
              ) : (
                <p className="text-foreground">{project.content}</p>
              )}
            </div>
            {/* Save Button */}
            {isEditing && !isContentLocked && (
              <Button onClick={handleSaveContent}>
                Lưu
              </Button>
            )}
            {/* Indicate the content lock status */}
            {isContentLocked && (
              <p className="text-sm text-destructive">Nội dung đã bị khóa bởi giảng viên</p>
            )}
            {showCommentSection && userRole === "instructor" && (
              <>
                <Separator />
                <div className="space-y-2">
                  <Textarea
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="Nhập nhận xét của bạn..."
                    className="min-h-[100px]"
                  />
                  <Button onClick={handleSubmitComment} disabled={!comment.trim()}>
                    Gửi nhận xét
                  </Button>
                </div>
              </>
            )}
          </CardContent>
        </Card>

        {/* Overall Progress Card */}
        <Card className="mb-8">
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span className="font-bold">Tiến độ tổng thể</span>
              <span className="text-2xl font-bold text-primary">{overallProgress}%</span>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ProjectProgressBar milestones={project.milestones} projectTotalTasks={totalTasks} projectId={project.id} />
            <div className="flex items-center justify-between mt-4 text-sm text-muted-foreground">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-success" />
                <span>{completedMilestones} Hoàn thành</span>
              </div>
              <div className="flex items-center gap-2">
                <Clock className="w-4 h-4 text-primary" />
                <span>{project.milestones.length - completedMilestones} Đang thực hiện</span>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Project Information */}
        <Card className="mb-8">
          <CardHeader>
            <CardTitle className="font-bold">Thông tin dự án</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-sm font-medium text-muted-foreground mb-1">Ngày bắt đầu</h3>
                <p className="text-foreground">{project.startDate.toLocaleDateString()}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-muted-foreground mb-1">Ngày kết thúc</h3>
                <p className="text-foreground">{project.endDate.toLocaleDateString()}</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-muted-foreground mb-1">Quy mô nhóm</h3>
                <p className="text-foreground">{project.memberCount} thành viên</p>
              </div>
              <div>
                <h3 className="text-sm font-medium text-muted-foreground mb-1">Trạng thái</h3>
                <p className="text-foreground">{statusInfo.label}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Milestones */}
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-foreground">Cột mốc</h2>
            {userRole === "student" && (
              <Button onClick={() => setCreatingMilestone(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Tạo cột mốc
              </Button>
            )}
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Existing Milestone Cards */}
            {project.milestones.map((milestone) => {
              return (
                <MilestoneCard
                  key={milestone.id}
                  id={milestone.id}
                  projectId={project.id}
                  title={milestone.title}
                  description={milestone.description}
                  startDate={milestone.startDate}
                  endDate={milestone.endDate}
                  completionPercentage={milestone.completionPercentage}
                  tasksTotal={milestone.tasksTotal}
                  tasksCompleted={milestone.tasksCompleted}
                  status={milestone.status}
                />
              )
            })}
            {/* New Milestone Card in creation mode */}
            {creatingMilestone && (
              <MilestoneCard
                projectId={project.id}
                // All fields empty or undefined
                autoFocus={true}
                onCancel={() => setCreatingMilestone(false)}
                onCreated={(newMilestone) => {
                  // Push into main list
                  setProject({ ...project, milestones: [...project.milestones, newMilestone] });
                  setCreatingMilestone(false);
                }}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
