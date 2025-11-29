import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "@/contexts/AuthContext";

import { ArrowLeft, Calendar, Edit, Lock, Unlock, Trash, Users } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Form, FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import { Checkbox } from "@/components/ui/checkbox";

import { ProgressReportThread } from "@/components/ProgressReportThread";
import { TaskDetail } from "@/types/task.type";
import { Report } from "@/types/report.type";
import { BaseUser } from "@/types/user.type";
import { taskService } from "@/services/task.service";
import { projectService } from "@/services/project.service";
import { getInitials } from "@/utils/user.utils";
import { toast } from "sonner";

import { taskSchema } from "@/zod_schema/task.schema";
import { z } from "zod";


type TaskFormValues = z.infer<typeof taskSchema>;


export const TaskDetailPage = () => {
  const { projectId, milestoneId, taskId } = useParams<{
    projectId: string;
    milestoneId: string;
    taskId: string;
  }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const userRole = user?.role === "INSTRUCTOR" ? "instructor" : "student";
  const [task, setTask] = useState<TaskDetail | null>(null);
  const [projectMembers, setProjectMembers] = useState<BaseUser[]>([]);
  const [isEditing, setIsEditing] = useState(false);
  const [selectedAssignees, setSelectedAssignees] = useState<number[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Initialize form
  const form = useForm<TaskFormValues>({
    resolver: zodResolver(taskSchema),
    defaultValues: {
      title: "",
      description: "",
      startDate: new Date().toISOString().split("T")[0],
      endDate: new Date().toISOString().split("T")[0],
      assigneeIds: [],
    },
  });

  // Fetch task detail on mount
  useEffect(() => {
    const fetchTaskDetail = async () => {
      try {
        setIsLoading(true);
        const response = await taskService.getTaskById(Number(taskId), true);
        
        if (response.status === "success" && response.data) {
          setTask(response.data as TaskDetail);
          setSelectedAssignees(response.data.assignees.map((a) => a.id));

          form.reset({
            title: response.data.title,
            description: response.data.description,
            startDate: new Date(response.data.startDate).toISOString().split("T")[0],
            endDate: new Date(response.data.endDate).toISOString().split("T")[0],
            assigneeIds: response.data.assignees.map((a) => a.id),
          });
        } else {
          toast.error(response.message || "Không thể tải thông tin nhiệm vụ");
        }
      } catch (error) {
        toast.error("Đã xảy ra lỗi khi tải thông tin nhiệm vụ");
      } finally {
        setIsLoading(false);
      }
    };

    fetchTaskDetail();
  }, [taskId, form]);

  // Fetch project members on mount
  useEffect(() => {
    const fetchProjectMembers = async () => {
      if (!projectId) return;
      
      try {
        const members = await projectService.getProjectMembers(Number(projectId));
        setProjectMembers(members);
      } catch (error) {
        console.error("Không thể tải danh sách thành viên:", error);
        toast.error("Không thể tải danh sách thành viên dự án");
      }
    };

    fetchProjectMembers();
  }, [projectId]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-extrabold text-foreground mb-4">Đang tải...</h1>
        </div>
      </div>
    );
  }

  if (!task) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-extrabold text-foreground mb-4">
            Không tìm thấy nhiệm vụ
          </h1>
          <Button
            onClick={() =>
              navigate(`/project/${projectId}/milestone/${milestoneId}`)
            }
          >
            Quay lại
          </Button>
        </div>
      </div>
    );
  }

  const isCompleted = task.status === "COMPLETED";
  const canEdit = userRole === "student" && !task.isLocked;
  const canLock = userRole === "instructor";

  // Handle task update
  const handleSaveEdit = async (data: TaskFormValues) => {
    try {
      const response = await taskService.updateTask(Number(taskId), {
        title: data.title,
        description: data.description,
        startDate: data.startDate,
        endDate: data.endDate,
        assigneeIds: selectedAssignees,
      });

      if (response.status === "success" && response.data) {
        setTask({
          ...task,
          title: response.data.title,
          description: response.data.description,
          startDate: new Date(response.data.startDate),
          endDate: new Date(response.data.endDate),
          assignees: response.data.assignees,
        });
        setIsEditing(false);
        toast.success("Cập nhật nhiệm vụ thành công");
      } else {
        toast.error(response.message || "Không thể cập nhật nhiệm vụ");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi cập nhật nhiệm vụ");
    }
  };

  const handleCancelEdit = () => {
    form.reset({
      title: task.title,
      description: task.description,
      startDate: new Date(task.startDate).toISOString().split("T")[0],
      endDate: new Date(task.endDate).toISOString().split("T")[0],
      assigneeIds: task.assignees.map((a) => a.id),
    });
    setSelectedAssignees(task.assignees.map((a) => a.id));
    setIsEditing(false);
  };

  // Handle task completion toggle for students
  const handleToggleComplete = async () => {
    try {
      const newStatus = isCompleted ? "IN_PROGRESS" : "COMPLETED";
      const response = await taskService.toggleTaskStatus(Number(taskId), newStatus);

      if (response.status === "success") {
        setTask({ ...task, status: newStatus });
        toast.success(
          newStatus === "COMPLETED"
            ? "Đã đánh dấu hoàn thành"
            : "Đã đánh dấu đang tiến hành"
        );
      } else {
        toast.error(response.message || "Không thể thay đổi trạng thái");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi thay đổi trạng thái");
    }
  };

  // Handle task lock toggle for instructor
  const handleToggleLock = async () => {
    try {
      const response = await taskService.toggleTaskLock(Number(taskId), {
        isLocked: !task.isLocked,
      });

      if (response.status === "success" && response.data) {
        setTask({ ...task, isLocked: response.data.isLocked });
        toast.success(
          response.data.isLocked
            ? "Đã khóa nhiệm vụ thành công"
            : "Đã mở khóa nhiệm vụ thành công"
        );
      } else {
        toast.error(response.message || "Không thể thay đổi trạng thái khóa");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi thay đổi trạng thái khóa");
    }
  };

  // Handle task delete
  const handleDelete = async () => {
    if (!confirm("Bạn có chắc chắn muốn xóa nhiệm vụ này?")) return;
    
    try {
      const response = await taskService.deleteTask(Number(taskId));

      if (response.status === "success") {
        toast.success("Xóa nhiệm vụ thành công");
        navigate(`/project/${projectId}/milestone/${milestoneId}`);
      } else {
        toast.error(response.message || "Không thể xóa nhiệm vụ");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi xóa nhiệm vụ");
    }
  };

  // Toggle assignee selection for update form
  const toggleAssignee = (memberId: number) => {
    setSelectedAssignees((prev) => {
      if (prev.includes(memberId)) {
        return prev.filter((id) => id !== memberId);
      } else {
        return [...prev, memberId];
      }
    });
  };

  // Report handlers
  // These handlers update the task's reports state when reports are created or updated
  // Don't implement the actual API calls here; just update local state

  // When a new report is created
  const handleReportCreated = (newReport: Report) => {
    if (!task) return;
    setTask({
      ...task,
      reports: [...task.reports, newReport],
    });
  };

  // When an existing report is updated
  const handleReportUpdated = (updatedReport: Report) => {
    if (!task) return;
    setTask({
      ...task,
      reports: task.reports.map(r => 
        r.id === updatedReport.id ? updatedReport : r
      ),
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-primary/5">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-6">
          <div className="flex items-center justify-between">
            <Button
              variant="ghost"
              size="sm"
              className="cursor-pointer"
              onClick={() =>
                navigate(`/project/${projectId}/milestone/${milestoneId}`)
              }
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              Quay lại
            </Button>

            {/* Action buttons */}
            <div className="flex items-center gap-2">
              {canEdit && !isEditing && (
                <>
                  <Button
                    size="sm"
                    variant="ghost"
                    onClick={() => setIsEditing(true)}
                  >
                    <Edit className="w-4 h-4" />
                  </Button>
                  <Button size="sm" variant="destructive" onClick={handleDelete}>
                    <Trash className="w-4 h-4" />
                  </Button>
                </>
              )}
              {isEditing && (
                <>
                  <Button size="sm" onClick={form.handleSubmit(handleSaveEdit)}>
                    Lưu
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={handleCancelEdit}
                  >
                    Hủy
                  </Button>
                </>
              )}
              {canLock && (
                <Button size="sm" variant="outline" onClick={handleToggleLock}>
                  {task.isLocked ? (
                    <Unlock className="w-4 h-4 mr-2" />
                  ) : (
                    <Lock className="w-4 h-4 mr-2" />
                  )}
                  {task.isLocked ? "Mở khóa" : "Khóa"}
                </Button>
              )}
            </div>
          </div>
        </div>

        <div className="flex flex-col sm:flex-row gap-6">
          {/* Task Information - Left Side on larger screens, top on mobile */}
          <div className="w-full sm:w-2/5 order-1">
            {/* Editing Mode */}
            {isEditing && (
              <Card>
                <CardHeader>
                  <CardTitle>Chỉnh sửa công việc</CardTitle>
                </CardHeader>
                <CardContent>
                  <Form {...form}>
                    <form
                      onSubmit={form.handleSubmit(handleSaveEdit)}
                      className="space-y-4"
                    >
                      <FormField
                        control={form.control}
                        name="title"
                        render={({ field }) => (
                          <FormItem>
                            <FormControl>
                              <Input
                                {...field}
                                placeholder="Tên công việc"
                                autoFocus
                                className="bg-white"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="description"
                        render={({ field }) => (
                          <FormItem>
                            <FormControl>
                              <Textarea
                                {...field}
                                placeholder="Mô tả công việc"
                                className="text-foreground w-full text-sm leading-relaxed min-h-[100px] bg-white"
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <div className="space-y-2">
                        <label className="text-sm font-medium text-foreground">Thời gian</label>
                        <div className="flex items-center gap-2">
                          <FormField
                            control={form.control}
                            name="startDate"
                            render={({ field }) => (
                              <FormItem className="flex-1">
                                <FormControl>
                                  <Input
                                    {...field}
                                    type="date"
                                    className="bg-white"
                                  />
                                </FormControl>
                                <FormMessage />
                              </FormItem>
                            )}
                          />

                          <span className="text-muted-foreground">-</span>

                          <FormField
                            control={form.control}
                            name="endDate"
                            render={({ field }) => (
                              <FormItem className="flex-1">
                                <FormControl>
                                  <Input
                                    {...field}
                                    type="date"
                                    className="bg-white"
                                  />
                                </FormControl>
                                <FormMessage />
                              </FormItem>
                            )}
                          />
                        </div>
                      </div>

                      <div className="space-y-2">
                        <label className="text-sm font-medium text-foreground">Đảm nhiệm</label>
                        <div className="space-y-2">
                          {projectMembers.map((member) => (
                            <div
                              key={member.id}
                              className="flex items-center gap-3 p-2 rounded-md hover:bg-secondary/50 transition-colors"
                            >
                              <Checkbox
                                checked={selectedAssignees.includes(member.id)}
                                className="w-5 h-5"
                                onCheckedChange={() => {
                                  toggleAssignee(member.id);
                                  form.setValue(
                                    "assigneeIds",
                                    selectedAssignees.includes(member.id)
                                      ? selectedAssignees.filter((id) => id !== member.id)
                                      : [...selectedAssignees, member.id]
                                  );
                                }}
                              />
                              <span className="text-sm text-foreground">{member.displayName}</span>
                            </div>
                          ))}
                        </div>
                        {form.formState.errors.assigneeIds && (
                          <p className="text-xs text-destructive">
                            {form.formState.errors.assigneeIds.message}
                          </p>
                        )}
                      </div>
                    </form>
                  </Form>
                </CardContent>
              </Card>
            )}

            {/* Display Mode */}
            {!isEditing && (
              <Card>
                <CardHeader>
                  <div className="flex items-center gap-3 flex-wrap">
                    <CardTitle className="text-2xl">{task.title}</CardTitle>
                    {task.isLocked && (
                      <Badge
                        variant="outline"
                        className="bg-destructive/10 text-destructive border-destructive/20"
                      >
                        <Lock className="w-3 h-3 mr-1" />
                        Đã khóa
                      </Badge>
                    )}
                    {isCompleted && (
                      <Badge
                        variant="outline"
                        className="bg-success/10 text-success border-success/20"
                      >
                        Hoàn thành
                      </Badge>
                    )}
                  </div>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <h3 className="text-sm font-semibold text-foreground mb-2">Mô tả</h3>
                    <p className="text-muted-foreground text-sm leading-relaxed">
                      {task.description}
                    </p>
                  </div>

                  <Separator />

                  <div>
                    <h3 className="text-sm font-semibold text-foreground mb-2">Thời gian</h3>
                    <div className="flex items-center gap-2 text-muted-foreground">
                      <Calendar className="w-4 h-4" />
                      <span className="text-sm">
                        {task.startDate.toLocaleDateString("vi-VN")} -{" "}
                        {task.endDate.toLocaleDateString("vi-VN")}
                      </span>
                    </div>
                  </div>

                  <Separator />

                  {/* Assignees Section */}
                  <div className="space-y-3">
                    <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
                      <Users className="w-4 h-4" />
                      <span>Đảm nhiệm</span>
                    </div>
                    <div className="space-y-2">
                      {task.assignees.map((assignee) => (
                        <div key={assignee.id} className="flex items-center gap-3">
                          <Avatar className="w-8 h-8">
                            <AvatarFallback className="bg-primary text-primary-foreground text-xs">
                              {getInitials(assignee.displayName)}
                            </AvatarFallback>
                          </Avatar>
                          <span className="text-sm font-medium text-foreground">
                            {assignee.displayName}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>

                  {canEdit && (
                    <>
                      <Separator />
                      <div className="flex items-center gap-3">
                        <Checkbox
                          checked={isCompleted}
                          onCheckedChange={handleToggleComplete}
                          disabled={task.isLocked}
                          className="w-5 h-5"
                        />
                        <label className="text-sm font-medium text-foreground cursor-pointer">
                          Đánh dấu là hoàn thành
                        </label>
                      </div>
                    </>
                  )}

                  {task.isLocked && (
                    <p className="text-sm text-destructive">
                      Công việc đã bị khóa bởi giảng viên
                    </p>
                  )}
                </CardContent>
              </Card>
            )}
          </div>

          {/* Progress Reports - Right Side on larger screens, bottom on mobile */}
          <div className="w-full sm:w-3/5 order-2">
            <ProgressReportThread
              taskId={task.id}
              projectId={Number(projectId)}
              milestoneId={Number(milestoneId)}
              reports={task.reports}
              projectMembers={projectMembers}
              userRole={userRole}
              isTaskLocked={task.isLocked}
              onReportCreated={handleReportCreated}
              onReportUpdated={handleReportUpdated}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
