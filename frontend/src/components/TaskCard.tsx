// TODO: Implement navigation to task detail page

import { useState, useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

import { Checkbox } from "@/components/ui/checkbox";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Form, FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form";
import { Calendar, Users, Lock, Edit, Trash, X } from "lucide-react";
import { useNavigate } from "react-router-dom";

import { taskSchema } from "@/zod_schema/task.schema";
import { Task } from "@/types/task.type";
import { BaseUser } from "@/types/user.type";
import { taskService } from "@/services/task.service";
import { getInitials } from "@/utils/user.utils";
import { toast } from "sonner";

interface TaskCardProps {
  id?: number;
  projectId: number;
  milestoneId: number;
  title?: string;
  description?: string;
  assignees?: BaseUser[];
  startDate?: Date;
  endDate?: Date;
  completed?: boolean;
  isLocked?: boolean;
  isMilestoneLocked?: boolean;
  userRole?: "student" | "instructor";
  onToggle?: () => void;
  onCancel?: () => void;
  onCreated?: (task: Task) => void;
  onUpdated?: (task: Task) => void;
  onDeleted?: (taskId: number) => void;
  autoFocus?: boolean;
  availableMembers?: BaseUser[];
}

export const TaskCard = ({
  id,
  projectId,
  milestoneId,
  title,
  description = "",
  assignees = [],
  startDate,
  endDate,
  completed = false,
  isLocked = false,
  isMilestoneLocked = false,
  userRole = "student",
  onToggle,
  onCancel,
  onCreated,
  onUpdated,
  onDeleted,
  autoFocus = false,
  availableMembers = [],
}: TaskCardProps) => {
  const navigate = useNavigate();
  
  // Effective lock status: task is locked if either the task itself or its parent milestone is locked
  const isEffectiveLocked = isLocked || isMilestoneLocked;
  
  // Ref for title input to focus when entering edit/create mode
  const titleRef = useRef<HTMLInputElement>(null);
  useEffect(() => {
    if (autoFocus && titleRef.current) {
      titleRef.current.scrollIntoView({ behavior: "smooth" });
      titleRef.current.focus();
    }
  }, [autoFocus]);

  // The card is new if no id provided
  const isNew = !id;

  // State to track if we are in editing mode
  const [isEditing, setIsEditing] = useState<boolean>(isNew);

  // Selected assignees for the multi-select
  const [selectedAssignees, setSelectedAssignees] = useState<number[]>(
    assignees.map(a => a.id)
  );

  // Helper function to safely format Date -> yyyy-mm-dd or ""
  const formatDateInput = (d?: Date) =>
    d ? d.toISOString().split("T")[0] : "";

  // Initial form values from props
  const initialValues = {
    title: title ?? "",
    description: description ?? "",
    startDate: formatDateInput(startDate),
    endDate: formatDateInput(endDate),
    assigneeIds: assignees.map(a => a.id),
  };

  // React Hook Form setup
  const form = useForm<z.infer<typeof taskSchema>>({
    resolver: zodResolver(taskSchema),
    defaultValues: initialValues,
  });

  // When user clicks the edit icon
  const handleEnterEdit = () => {
    form.reset(initialValues);
    setSelectedAssignees(assignees.map(a => a.id));
    setIsEditing(true);
  };

  // Cancel editing
  const handleCancel = () => {
    if (isNew) {
      onCancel?.();
      return;
    }
    form.reset(initialValues);
    setSelectedAssignees(assignees.map(a => a.id));
    setIsEditing(false);
  };

  // Handle delete
  const handleDelete = async () => {
    if (!id) return;
    
    try {
      const response = await taskService.deleteTask(id);
      
      if (response.status === "success") {
        onDeleted?.(id);
        toast.success("Xóa nhiệm vụ thành công");
      } else {
        toast.error(response.message || "Không thể xóa nhiệm vụ");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi xóa nhiệm vụ");
    }
  };

  // Toggle assignee selection
  const toggleAssignee = (memberId: number) => {
    setSelectedAssignees(prev => {
      if (prev.includes(memberId)) {
        return prev.filter(id => id !== memberId);
      } else {
        return [...prev, memberId];
      }
    });
  };

  // On form submit
  const onSubmit = async (data: z.infer<typeof taskSchema>) => {
    try {
      if (isNew) {
        // Create new task via API
        const response = await taskService.createTask({
          milestoneId: milestoneId,
          projectId: projectId,
          title: data.title,
          description: data.description || "",
          startDate: data.startDate,
          endDate: data.endDate,
          assigneeIds: selectedAssignees,
        });

        if (response.status === "success" && response.data) {
          onCreated?.(response.data);
          toast.success("Tạo nhiệm vụ thành công");
        } else {
          toast.error(response.message || "Không thể tạo nhiệm vụ");
        }
      } else {
        // Update existing task via API
        const response = await taskService.updateTask(id!, {
          title: data.title,
          description: data.description || "",
          startDate: data.startDate,
          endDate: data.endDate,
          assigneeIds: selectedAssignees,
        });

        if (response.status === "success" && response.data) {
          onUpdated?.(response.data);
          toast.success("Cập nhật nhiệm vụ thành công");
          setIsEditing(false);
        } else {
          toast.error(response.message || "Không thể cập nhật nhiệm vụ");
        }
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi lưu nhiệm vụ");
    }
  };

  const handleClick = () => {
    if (!isEditing && id) {
      navigate(`/project/${projectId}/milestone/${milestoneId}/task/${id}`);
    }
  };

  return (
    <div className="flex items-start gap-3 p-4 rounded-lg border border-border bg-card hover:bg-secondary/50 transition-colors">
      {/* Display Mode */}
      {!isEditing && !isNew && (
        <>
          <Checkbox 
            checked={completed} 
            onCheckedChange={onToggle}
            className="mt-1"
            disabled={isEffectiveLocked}
          />
          <div className="flex-1 space-y-2 cursor-pointer" onClick={handleClick}>
            <div className="flex items-start justify-between gap-2">
              <div className="flex items-center gap-2">
                <h4 className={`font-medium text-foreground ${completed ? "line-through text-muted-foreground" : ""}`}>
                  {title}
                </h4>
                {isEffectiveLocked && (
                  <Lock className="w-3 h-3 text-destructive" />
                )}
              </div>
            </div>
            <div className="flex items-center gap-4 text-sm text-muted-foreground">
              <div className="flex items-center gap-1">
                <Users className="w-3 h-3" />
                <div className="flex -space-x-2">
                  {assignees.slice(0, 3).map((assignee) => (
                    <Avatar key={assignee.id} className="w-5 h-5 border-2 border-card">
                      <AvatarFallback className="bg-primary text-primary-foreground text-[10px]">
                        {getInitials(assignee.displayName)}
                      </AvatarFallback>
                    </Avatar>
                  ))}
                  {assignees.length > 3 && (
                    <div className="w-5 h-5 rounded-full bg-muted border-2 border-card flex items-center justify-center">
                      <span className="text-[10px] text-muted-foreground">+{assignees.length - 3}</span>
                    </div>
                  )}
                </div>
              </div>
              <div className="flex items-center gap-1">
                <Calendar className="w-3 h-3" />
                <span>{endDate?.toLocaleDateString()}</span>
              </div>
            </div>
          </div>
          
          {/* Edit/Delete buttons for students when not locked */}
          {userRole === "student" && !isEffectiveLocked && (
            <div className="flex items-center gap-1">
              <Button
                size="icon"
                variant="ghost"
                className="h-6 w-6"
                onClick={(e) => {
                  e.stopPropagation();
                  handleEnterEdit();
                }}
              >
                <Edit className="w-3 h-3" />
              </Button>
              <Button
                size="icon"
                variant="ghost"
                className="h-6 w-6 text-destructive hover:text-destructive"
                onClick={(e) => {
                  e.stopPropagation();
                  handleDelete();
                }}
              >
                <Trash className="w-3 h-3" />
              </Button>
            </div>
          )}
        </>
      )}

      {/* Edit/Create Mode */}
      {isEditing && (
        <div className="flex-1">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-3">
              {/* Title Field */}
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>
                      <Input
                        {...field}
                        placeholder="Tên công việc"
                        ref={titleRef}
                        className="bg-white"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Description Field */}
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>
                      <Textarea
                        {...field}
                        placeholder="Mô tả công việc"
                        rows={2}
                        className="bg-white"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Date Range */}
              <div className="flex items-center gap-2 text-sm">
                <Calendar className="w-3 h-3 text-muted-foreground" />
                
                <FormField
                  control={form.control}
                  name="startDate"
                  render={({ field }) => (
                    <FormItem>
                      <FormControl>
                        <Input
                          {...field}
                          type="date"
                          className="h-7 bg-white text-xs"
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
                    <FormItem>
                      <FormControl>
                        <Input
                          {...field}
                          type="date"
                          className="h-7 bg-white text-xs"
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              {/* Assignees Selection */}
              <div className="space-y-2">
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Users className="w-3 h-3" />
                  <span>Đảm nhiệm:</span>
                </div>
                <div className="flex flex-wrap gap-2">
                  {availableMembers.map((member) => (
                    <button
                      key={member.id}
                      type="button"
                      onClick={() => {
                        toggleAssignee(member.id);
                        form.setValue('assigneeIds', selectedAssignees.includes(member.id) 
                          ? selectedAssignees.filter(id => id !== member.id)
                          : [...selectedAssignees, member.id]
                        );
                      }}
                      className={`flex items-center gap-1 px-2 py-1 rounded-md text-xs border transition-colors ${
                        selectedAssignees.includes(member.id)
                          ? "bg-primary text-primary-foreground border-primary"
                          : "bg-white border-border hover:bg-secondary"
                      }`}
                    >
                      <Avatar className="w-4 h-4">
                        <AvatarFallback className="text-[8px]">
                          {getInitials(member.displayName)}
                        </AvatarFallback>
                      </Avatar>
                      <span>{member.displayName}</span>
                      {selectedAssignees.includes(member.id) && (
                        <X className="w-3 h-3" />
                      )}
                    </button>
                  ))}
                </div>
                {form.formState.errors.assigneeIds && (
                  <p className="text-xs text-destructive">
                    {form.formState.errors.assigneeIds.message}
                  </p>
                )}
              </div>

              {/* Action Buttons */}
              <div className="flex gap-2">
                <Button type="submit" size="sm" className="cursor-pointer">
                  Lưu
                </Button>
                <Button
                  type="button"
                  size="sm"
                  variant="ghost"
                  className="cursor-pointer"
                  onClick={handleCancel}
                >
                  Hủy
                </Button>
              </div>
            </form>
          </Form>
        </div>
      )}
    </div>
  );
};
