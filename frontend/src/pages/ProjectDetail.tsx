import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { CheckCircle2, Clock, Lock, Unlock, Plus, Edit } from "lucide-react";
import { projectService } from "@/services/project.service";
import { useAuth } from "@/contexts/AuthContext";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { Separator } from "@/components/ui/separator";

import { ProjectDetail } from "@/types/project.type";
import { statusConfig } from "@/types/project.type";
import { MilestoneCard } from "@/components/MilestoneCard";
import { ProjectProgressBar } from "@/components/ProjectProgressBar";
import { toast } from "sonner";


export const ProjectDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const userRole = user?.role === "INSTRUCTOR" ? "instructor" : "student";

  // Project content state
  const [project, setProject] = useState<ProjectDetail | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Editing state - only store temporary edits
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [editedObjective, setEditedObjective] = useState<string>("");
  const [editedContent, setEditedContent] = useState<string>("");

  // State to check if creating new milestone or not
  const [creatingMilestone, setCreatingMilestone] = useState<boolean>(false);

  // State for instructor to add comments
  const [comment, setComment] = useState<string>("");
  const [showCommentSection, setShowCommentSection] = useState<boolean>(false);

  // Fetch project detail on mount
  useEffect(() => {
    const loadProjectDetail = async () => {
      try {
        setIsLoading(true);
        const response = await projectService.getProjectById(Number(id));
        
        if (response.status === 200 && response.data) {
          setProject(response.data);
        } else {
          toast.error(response.message || "Không thể tải thông tin dự án");
        }
      } catch (error) {
        toast.error("Đã xảy ra lỗi khi tải thông tin dự án");
      } finally {
        setIsLoading(false);
      }
    };
    
    loadProjectDetail();
  }, [id]);

  // Handle loading state
  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-extrabold text-foreground mb-4">Đang tải...</h1>
        </div>
      </div>
    );
  }

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
  const handleStartEditing = () => {
    setEditedObjective(project.objective);
    setEditedContent(project.content);
    setIsEditing(true);
  };

  // Event handler to cancel editing
  const handleCancelEditing = () => {
    setIsEditing(false);
    setEditedObjective("");
    setEditedContent("");
  };

  const handleSaveContent = async () => {
    try {
      const response = await projectService.updateProjectContent(Number(id), {
        objective: editedObjective,
        content: editedContent,
      });
      
      if (response.status === 200) {
        setProject({ ...project, objective: editedObjective, content: editedContent });
        setIsEditing(false);
        setEditedObjective("");
        setEditedContent("");
        toast.success("Cập nhật nội dung thành công");
      } else {
        toast.error(response.message || "Không thể cập nhật nội dung");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi cập nhật nội dung");
    }
  };

  // Event handler for locking/unlocking content & objective
  const handleToggleLock = async () => {
    try {
      if (project.isObjDesLocked) {
        const response = await projectService.unlockProject(Number(id));
        if (response.status === 200) {
          setProject({ ...project, isObjDesLocked: false });
          toast.success("Đã mở khóa nội dung");
        } else {
          toast.error(response.message || "Không thể mở khóa nội dung");
        }
      } else {
        const response = await projectService.lockProject(Number(id));
        if (response.status === 200) {
          setProject({ ...project, isObjDesLocked: true });
          toast.success("Đã khóa nội dung");
        } else {
          toast.error(response.message || "Không thể khóa nội dung");
        }
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi thay đổi trạng thái khóa");
    }
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
                      {project.isObjDesLocked ? <Unlock className="w-4 h-4 mr-2" /> : <Lock className="w-4 h-4 mr-2" />}
                      {project.isObjDesLocked ? "Mở khóa nội dung" : "Khóa nội dung"}
                    </Button>
                  </>
                )}
                {userRole === "student" && !project.isObjDesLocked && (
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => isEditing ? handleCancelEditing() : handleStartEditing()}
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
              {isEditing && !project.isObjDesLocked ? (
                <Textarea
                  value={editedObjective}
                  onChange={(e) => setEditedObjective(e.target.value)}
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
              {isEditing && !project.isObjDesLocked ? (
                <Textarea
                  value={editedContent}
                  onChange={(e) => setEditedContent(e.target.value)}
                  placeholder="Nhập mô tả dự án..."
                  className="min-h-[100px]"
                />
              ) : (
                <p className="text-foreground">{project.content}</p>
              )}
            </div>
            {/* Save Button */}
            {isEditing && !project.isObjDesLocked && (
              <Button onClick={handleSaveContent}>
                Lưu
              </Button>
            )}
            {/* Indicate the content lock status */}
            {project.isObjDesLocked && (
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
                <p className="text-foreground">{project.totalMembers} thành viên</p>
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
                  userRole={userRole}
                  onUpdated={(updatedMilestone) => {
                    setProject({
                      ...project,
                      milestones: project.milestones.map((m) =>
                        m.id === updatedMilestone.id ? { ...m, ...updatedMilestone } : m
                      ),
                    });
                  }}
                  onDeleted={(deletedId) => {
                    setProject({
                      ...project,
                      milestones: project.milestones.filter((m) => m.id !== deletedId),
                    });
                  }}
                />
              )
            })}
            {/* New Milestone Card in creation mode */}
            {creatingMilestone && (
              <MilestoneCard
                projectId={project.id}
                userRole={userRole}
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
