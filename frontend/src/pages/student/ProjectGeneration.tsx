import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Input } from "@/components/ui/input";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import {
  Calendar, Users, Sparkles, Loader2, ChevronDown, ChevronUp, Edit, Trash2, UserPlus, Send, CheckCircle, ArrowLeft
} from "lucide-react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { getInitials } from "@/utils/user.utils";
import { BaseUser } from "@/types/user.type";
import { GenProjectTree } from "@/types/project.type";

// Mock project data - replace with actual API call
const mockProject = {
  id: 1,
  title: "Phát triển hệ thống quản lý dự án",
  content: "Xây dựng hệ thống quản lý dự án cho sinh viên và giảng viên",
  objectives: "Tạo nền tảng theo dõi tiến độ dự án hiệu quả",
  startDate: new Date("2024-01-15"),
  endDate: new Date("2024-06-30"),
  students: [
    { id: 1, username: "student1", email: "student1@example.com", displayName: "Nguyễn Văn A", role: "STUDENT" as const },
    { id: 2, username: "student2", email: "student2@example.com", displayName: "Trần Thị B", role: "STUDENT" as const },
  ],
};


export const ProjectGeneration = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();

  // State variables
  const [description, setDescription] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);
  const [generatedProject, setGeneratedProject] = useState<GenProjectTree | null>(null);
  const [editingMilestone, setEditingMilestone] = useState<number | null>(null);
  const [editingTask, setEditingTask] = useState<{ milestoneIdx: number; taskIdx: number } | null>(null);

  const handleGenerate = async () => {
    if (!description.trim()) {
      toast.error("Vui lòng nhập mô tả dự án");
      return;
    }

    setIsGenerating(true);
    try {
      // Call AI service
      const response = await fetch("http://localhost:3030/api/generate-project", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          projectTitle: mockProject.title,
          projectDescription: description,
          startDate: mockProject.startDate.toISOString(),
          endDate: mockProject.endDate.toISOString(),
        }),
      });

      if (!response.ok) throw new Error("Failed to generate project");

      const data = await response.json();

      // Transform dates from strings to Date objects and add expanded state
      const transformedData: GenProjectTree = {
        ...data,
        milestones: data.milestones.map((milestone: any) => ({
          ...milestone,
          startDate: new Date(milestone.startDate),
          endDate: new Date(milestone.endDate),
          isExpanded: false,
          tasks: milestone.tasks.map((task: any) => ({
            ...task,
            startDate: new Date(task.startDate),
            endDate: new Date(task.endDate),
            assignees: [],
          })),
        })),
      };

      setGeneratedProject(transformedData);
      toast.success("Đã tạo kế hoạch dự án thành công!");
    } catch (error) {
      console.error(error);
      toast.error("Không thể tạo kế hoạch dự án");
    } finally {
      setIsGenerating(false);
    }
  };

  // Toggle milestone expansion
  const toggleMilestone = (index: number) => {
    if (!generatedProject) return;
    const updated = { ...generatedProject };
    updated.milestones[index].isExpanded = !updated.milestones[index].isExpanded;
    setGeneratedProject(updated);
  };

  // Delete milestone
  const deleteMilestone = (index: number) => {
    if (!generatedProject) return;
    const updated = { ...generatedProject };
    updated.milestones.splice(index, 1);
    setGeneratedProject(updated);
    toast.success("Đã xóa cột mốc");
  };

  // Delete task
  const deleteTask = (milestoneIdx: number, taskIdx: number) => {
    if (!generatedProject) return;
    const updated = { ...generatedProject };
    updated.milestones[milestoneIdx].tasks.splice(taskIdx, 1);
    setGeneratedProject(updated);
    toast.success("Đã xóa nhiệm vụ");
  };

  // Update milestone field
  const updateMilestone = (index: number, field: keyof GenProjectTree['milestones'][0], value: any) => {
    if (!generatedProject) return;
    const updated = { ...generatedProject };
    (updated.milestones[index] as any)[field] = value;
    setGeneratedProject(updated);
  };

  // Update task field
  const updateTask = (milestoneIdx: number, taskIdx: number, field: keyof GenProjectTree['milestones'][0]['tasks'][0], value: any) => {
    if (!generatedProject) return;
    const updated = { ...generatedProject };
    (updated.milestones[milestoneIdx].tasks[taskIdx] as any)[field] = value;
    setGeneratedProject(updated);
  };

  // Add assignee to task
  const addAssignee = (milestoneIdx: number, taskIdx: number, student: BaseUser) => {
    if (!generatedProject) return;
    const updated = { ...generatedProject };
    const task = updated.milestones[milestoneIdx].tasks[taskIdx];
    if (!task.assignees.find(a => a.id === student.id)) {
      task.assignees.push(student);
      setGeneratedProject(updated);
    }
  };

  // Remove assignee from task
  const removeAssignee = (milestoneIdx: number, taskIdx: number, studentId: number) => {
    if (!generatedProject) return;
    const updated = { ...generatedProject };
    const task = updated.milestones[milestoneIdx].tasks[taskIdx];
    task.assignees = task.assignees.filter(a => a.id !== studentId);
    setGeneratedProject(updated);
  };

  // Confirm and create project with generated data
  const handleConfirm = async () => {
    if (!generatedProject) return;

    // Validate that all tasks have at least one assignee
    for (let i = 0; i < generatedProject.milestones.length; i++) {
      for (let j = 0; j < generatedProject.milestones[i].tasks.length; j++) {
        if (generatedProject.milestones[i].tasks[j].assignees.length === 0) {
          toast.error(`Vui lòng chọn người thực hiện cho nhiệm vụ "${generatedProject.milestones[i].tasks[j].title}"`);
          return;
        }
      }
    }

    // Mock API call
    toast.success("Đang tạo dự án...");
    setTimeout(() => {
      toast.success("Đã tạo dự án thành công!");
      navigate(`/student/projects/${projectId}`);
    }, 1500);
  };

  // Helper to format date
  const formatDate = (date: Date) => {
    return date.toLocaleDateString("vi-VN");
  };

  return (
    <div className="container mx-auto py-6 space-y-6">
      {/* Back Button */}
      <Button
        variant="ghost"
        onClick={() => navigate(`/project/${projectId}`)}
        className="gap-2 cursor-pointer"
      >
        <ArrowLeft className="w-4 h-4" />
        Quay lại
      </Button>

      {/* Project Info */}
      <Card>
        <CardContent className="pt-6 space-y-6">
          {/* Project Title */}
          <div className="text-center">
            <h1 className="text-2xl font-extrabold">{mockProject.title}</h1>
          </div>

          {/* Content and Objectives */}
          <div className="space-y-4">
            <div>
              <label className="text-sm font-medium text-muted-foreground mb-1.5 block">Nội dung</label>
              <p className="text-sm leading-relaxed">{mockProject.content}</p>
            </div>
            <div>
              <label className="text-sm font-medium text-muted-foreground mb-1.5 block">Mục tiêu</label>
              <p className="text-sm leading-relaxed">{mockProject.objectives}</p>
            </div>
          </div>

          <Separator />

          {/* Bottom Section: Timeline and Members */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div className="flex flex-wrap items-center gap-6">
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-muted-foreground" />
                <span className="text-sm">
                  <span className="font-medium">Thời gian: </span>
                  {formatDate(mockProject.startDate)} - {formatDate(mockProject.endDate)}
                </span>
              </div>
              <div className="flex items-center gap-2">
                <Users className="w-4 h-4 text-muted-foreground" />
                <span className="text-sm">
                  <span className="font-medium">Thành viên: </span>
                  {mockProject.students.length}
                </span>
              </div>
            </div>
            
            <div className="flex flex-wrap gap-2">
              {mockProject.students.map((student) => (
                <Badge key={student.id} variant="secondary" className="flex items-center gap-1.5">
                  <Avatar className="w-4 h-4">
                    <AvatarFallback className="text-[10px]">
                      {getInitials(student.displayName)}
                    </AvatarFallback>
                  </Avatar>
                  <span className="text-xs">{student.displayName}</span>
                </Badge>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* AI Generation Input */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Sparkles className="w-5 h-5" />
            Tạo kế hoạch dự án với AI
          </CardTitle>
          <CardDescription>
            Mô tả chi tiết về dự án để AI tạo kế hoạch cột mốc và nhiệm vụ
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <Textarea
            placeholder="Ví dụ: Xây dựng một ứng dụng web quản lý thư viện với các tính năng: quản lý sách, mượn trả sách, tìm kiếm, báo cáo thống kê..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={6}
            disabled={isGenerating}
          />
          <Button
            onClick={handleGenerate}
            disabled={isGenerating || !description.trim()}
            className="w-full cursor-pointer"
          >
            {isGenerating ? (
              <>
                <Loader2 className="w-4 h-4 animate-spin mr-2" />
                Đang tạo kế hoạch...
              </>
            ) : (
              <>
                <Send className="w-4 h-4 mr-2" />
                Tạo kế hoạch
              </>
            )}
          </Button>
        </CardContent>
      </Card>

      {/* Generated Result */}
      {generatedProject && (
        <Card>
          <CardHeader>
            <CardTitle>Kế hoạch được tạo</CardTitle>
            <CardDescription>
              Xem xét và chỉnh sửa kế hoạch trước khi xác nhận
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Generated Content & Objectives */}
            <div className="space-y-4">
              <div>
                <label className="text-sm font-medium text-muted-foreground">Nội dung</label>
                <Textarea
                  value={generatedProject.content}
                  onChange={(e) => setGeneratedProject({ ...generatedProject, content: e.target.value })}
                  rows={3}
                  className="mt-1"
                />
              </div>
              <div>
                <label className="text-sm font-medium text-muted-foreground">Mục tiêu</label>
                <Textarea
                  value={generatedProject.objectives}
                  onChange={(e) => setGeneratedProject({ ...generatedProject, objectives: e.target.value })}
                  rows={3}
                  className="mt-1"
                />
              </div>
            </div>

            <Separator />

            {/* Milestones */}
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold">Cột mốc & Nhiệm vụ</h3>
                <Badge variant="secondary">{generatedProject.milestones.length} cột mốc</Badge>
              </div>

              {generatedProject.milestones.map((milestone, mIdx) => (
                <Card key={mIdx} className="overflow-hidden">
                  {/* Milestone Header */}
                  <div className="bg-gray-50 border-l-4 border-l-primary">
                    <CardHeader className="pb-4">
                      <div className="space-y-3">
                        {/* Title with action buttons */}
                        <div className="flex items-start justify-between gap-4">
                          <div className="flex-1">
                            {editingMilestone === mIdx ? (
                              <Input
                                value={milestone.title}
                                onChange={(e) => updateMilestone(mIdx, "title", e.target.value)}
                                className="font-semibold"
                                autoFocus
                              />
                            ) : (
                              <div className="flex items-center gap-2">
                                <span className="text-sm font-medium text-muted-foreground">Cột mốc {mIdx + 1}:</span>
                                <h4 className="font-semibold">{milestone.title}</h4>
                              </div>
                            )}
                          </div>

                          <div className="flex items-center gap-1">
                            {editingMilestone === mIdx ? (
                              <Button
                                size="icon"
                                variant="ghost"
                                onClick={() => setEditingMilestone(null)}
                                className="h-8 w-8 cursor-pointer"
                              >
                                <CheckCircle className="w-4 h-4" />
                              </Button>
                            ) : (
                              <Button
                                size="icon"
                                variant="ghost"
                                onClick={() => setEditingMilestone(mIdx)}
                                className="h-8 w-8 cursor-pointer"
                              >
                                <Edit className="w-4 h-4" />
                              </Button>
                            )}
                            <Button
                              size="icon"
                              variant="ghost"
                              onClick={() => deleteMilestone(mIdx)}
                              className="h-8 w-8 text-destructive hover:text-destructive cursor-pointer"
                            >
                              <Trash2 className="w-4 h-4" />
                            </Button>
                            <Button
                              size="icon"
                              variant="ghost"
                              onClick={() => toggleMilestone(mIdx)}
                              className="h-8 w-8 cursor-pointer"
                            >
                              {milestone.isExpanded ? (
                                <ChevronUp className="w-4 h-4" />
                              ) : (
                                <ChevronDown className="w-4 h-4" />
                              )}
                            </Button>
                          </div>
                        </div>

                        {/* Description */}
                        {editingMilestone === mIdx ? (
                          <Textarea
                            value={milestone.description}
                            onChange={(e) => updateMilestone(mIdx, "description", e.target.value)}
                            rows={2}
                            className="text-sm"
                          />
                        ) : (
                          <p className="text-sm text-muted-foreground">{milestone.description}</p>
                        )}

                        {/* Date and metadata */}
                        <div className="flex flex-wrap items-center gap-3">
                          {editingMilestone === mIdx ? (
                            <div className="flex items-center gap-2 text-sm">
                              <Calendar className="w-4 h-4 text-muted-foreground" />
                              <Input
                                type="date"
                                value={milestone.startDate.toISOString().split("T")[0]}
                                onChange={(e) => updateMilestone(mIdx, "startDate", new Date(e.target.value))}
                                className="h-8 w-36"
                              />
                              <span>đến</span>
                              <Input
                                type="date"
                                value={milestone.endDate.toISOString().split("T")[0]}
                                onChange={(e) => updateMilestone(mIdx, "endDate", new Date(e.target.value))}
                                className="h-8 w-36"
                              />
                            </div>
                          ) : (
                            <>
                              <div className="flex items-center gap-1.5 text-sm">
                                <Calendar className="w-4 h-4 text-muted-foreground" />
                                <span>{formatDate(milestone.startDate)} - {formatDate(milestone.endDate)}</span>
                              </div>
                              <Badge variant="outline" className="gap-1">
                                <span>{milestone.tasks.length}</span>
                                <span>nhiệm vụ</span>
                              </Badge>
                            </>
                          )}
                        </div>
                      </div>
                    </CardHeader>
                  </div>

                  {/* Tasks List */}
                  <CardContent 
                    className={`border-l-4 border-l-blue-400 overflow-hidden transition-all duration-300 ease-in-out ${
                      milestone.isExpanded ? 'max-h-[2000px] pt-4 opacity-100' : 'max-h-0 p-0 opacity-0'
                    }`}
                  >
                    {milestone.tasks.length > 0 && (
                      <div className="space-y-3">
                        {milestone.tasks.map((task, tIdx) => (
                          <div
                            key={tIdx}
                            className="p-4 rounded-lg border bg-card"
                          >
                            <div className="space-y-3">
                              {/* Task Title with action buttons */}
                              <div className="flex items-start justify-between gap-4">
                                <div className="flex-1">
                                  {editingTask?.milestoneIdx === mIdx && editingTask?.taskIdx === tIdx ? (
                                    <Input
                                      value={task.title}
                                      onChange={(e) => updateTask(mIdx, tIdx, "title", e.target.value)}
                                      className="font-medium"
                                    />
                                  ) : (
                                    <div className="flex items-center gap-2">
                                      <span className="text-xs font-medium text-muted-foreground">Nhiệm vụ {tIdx + 1}:</span>
                                      <h5 className="font-medium">{task.title}</h5>
                                    </div>
                                  )}
                                </div>

                                <div className="flex items-center gap-1">
                                  {editingTask?.milestoneIdx === mIdx && editingTask?.taskIdx === tIdx ? (
                                    <Button
                                      size="icon"
                                      variant="ghost"
                                      onClick={() => setEditingTask(null)}
                                      className="h-8 w-8 cursor-pointer"
                                    >
                                      <CheckCircle className="w-3.5 h-3.5" />
                                    </Button>
                                  ) : (
                                    <Button
                                      size="icon"
                                      variant="ghost"
                                      onClick={() => setEditingTask({ milestoneIdx: mIdx, taskIdx: tIdx })}
                                      className="h-8 w-8 cursor-pointer"
                                    >
                                      <Edit className="w-3.5 h-3.5" />
                                    </Button>
                                  )}
                                  <Button
                                    size="icon"
                                    variant="ghost"
                                    onClick={() => deleteTask(mIdx, tIdx)}
                                    className="h-8 w-8 text-destructive hover:text-destructive cursor-pointer"
                                  >
                                    <Trash2 className="w-3.5 h-3.5" />
                                  </Button>
                                </div>
                              </div>

                              {/* Task Description */}
                              {editingTask?.milestoneIdx === mIdx && editingTask?.taskIdx === tIdx ? (
                                <Textarea
                                  value={task.description}
                                  onChange={(e) => updateTask(mIdx, tIdx, "description", e.target.value)}
                                  rows={2}
                                  className="text-sm"
                                />
                              ) : (
                                <p className="text-sm text-muted-foreground">{task.description}</p>
                              )}

                              {/* Task Date */}
                              <div className="flex items-center gap-2">
                                <Calendar className="w-3.5 h-3.5 text-muted-foreground" />
                                {editingTask?.milestoneIdx === mIdx && editingTask?.taskIdx === tIdx ? (
                                  <div className="flex items-center gap-2 text-xs">
                                    <Input
                                      type="date"
                                      value={task.startDate.toISOString().split("T")[0]}
                                      onChange={(e) => updateTask(mIdx, tIdx, "startDate", new Date(e.target.value))}
                                      className="h-8 w-36 text-xs"
                                    />
                                    <span>đến</span>
                                    <Input
                                      type="date"
                                      value={task.endDate.toISOString().split("T")[0]}
                                      onChange={(e) => updateTask(mIdx, tIdx, "endDate", new Date(e.target.value))}
                                      className="h-8 w-36 text-xs"
                                    />
                                  </div>
                                ) : (
                                  <span className="text-xs text-muted-foreground">
                                    {formatDate(task.startDate)} - {formatDate(task.endDate)}
                                  </span>
                                )}
                              </div>

                              {/* Task Assignees */}
                              <div className="space-y-2">
                                <label className="text-xs font-medium text-muted-foreground">
                                  Người thực hiện {task.assignees.length === 0 && <span className="text-destructive">*</span>}
                                </label>
                                {task.assignees.length > 0 && (
                                  <div className="flex flex-wrap gap-1.5 mt-1">
                                    {task.assignees.map((assignee) => (
                                      <Badge key={assignee.id} variant="secondary" className="flex items-center gap-1 px-2 py-0.5">
                                        <Avatar className="w-3.5 h-3.5">
                                          <AvatarFallback className="text-[9px]">
                                            {getInitials(assignee.displayName)}
                                          </AvatarFallback>
                                        </Avatar>
                                        <span className="text-xs">{assignee.displayName}</span>
                                      </Badge>
                                    ))}
                                  </div>
                                )}
                                {task.assignees.length === 0 && (
                                  <p className="text-xs text-destructive">Chưa chọn người thực hiện</p>
                                )}
                                
                                <div className="pt-1">
                                  <label className="text-xs font-medium text-muted-foreground block mb-1.5">
                                    Thành viên dự án
                                  </label>
                                  <div className="flex flex-wrap gap-1.5">
                                    {mockProject.students.map((student) => (
                                      <Button
                                        key={student.id}
                                        size="sm"
                                        variant={task.assignees.find(a => a.id === student.id) ? "default" : "outline"}
                                        onClick={() => {
                                          if (task.assignees.find(a => a.id === student.id)) {
                                            removeAssignee(mIdx, tIdx, student.id);
                                          } else {
                                            addAssignee(mIdx, tIdx, student);
                                          }
                                        }}
                                        className="h-7 px-2 text-xs cursor-pointer"
                                      >
                                        {student.displayName}
                                      </Button>
                                    ))}
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>

            <Separator />

            {/* Confirm Button */}
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button className="w-full" size="lg">
                  <CheckCircle className="w-5 h-5 mr-2" />
                  Xác nhận tạo dự án
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Xác nhận tạo dự án?</AlertDialogTitle>
                  <AlertDialogDescription>
                    Bạn có chắc chắn muốn tạo dự án với kế hoạch này? Hành động này sẽ tạo tất cả các cột mốc và nhiệm vụ vào hệ thống.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Hủy</AlertDialogCancel>
                  <AlertDialogAction onClick={handleConfirm}>
                    Xác nhận
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </CardContent>
        </Card>
      )}
    </div>
  );
};
