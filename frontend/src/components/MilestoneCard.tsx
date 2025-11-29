import { useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { CheckCircle2, Clock, SquarePen, Lock, Trash } from "lucide-react";
import { Button } from "./ui/button";

import {
  Form,
  FormField,
  FormItem,
  FormControl,
  FormMessage,
} from "@/components/ui/form";

import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Milestone } from "@/types/milestone.type";
import { milestoneSchema } from "@/zod_schema/milestone.schema";
import { milestoneService } from "@/services/milestone.service";
import { toast } from "sonner";

interface MilestoneCardProps {
  id?: number;
  projectId: number;
  title?: string;
  description?: string;
  startDate?: Date;
  endDate?: Date;
  completionPercentage?: number;
  tasksTotal?: number;
  tasksCompleted?: number;
  status?: "COMPLETED" | "IN_PROGRESS";
  isLocked?: boolean;
  userRole?: "student" | "instructor";
  onCancel?: () => void;
  onCreated?: (milestone: Milestone) => void;
  onUpdated?: (milestone: Milestone) => void;
  onDeleted?: (milestoneId: number) => void;
  autoFocus?: boolean; // whether to auto-focus the first input when in edit/create mode
}

export const MilestoneCard = ({
  id,
  projectId,
  title,
  description,
  startDate,
  endDate,
  completionPercentage = 0,
  tasksTotal = 0,
  tasksCompleted = 0,
  status,
  isLocked = false,
  userRole = "student",
  onCancel,
  onCreated,
  onUpdated,
  onDeleted,
  autoFocus = false,
}: MilestoneCardProps) => {
  const navigate = useNavigate();

  // Ref of title input to focus when entering edit/create mode
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
  // Start editing immediately if new
  const [isEditing, setIsEditing] = useState<boolean>(isNew);

  // Helper function to safely format Date -> yyyy-mm-dd or ""
  const formatDateInput = (d?: Date) =>
    d ? d.toISOString().split("T")[0] : "";

  // Initial form values from props
  const initialValues = {
    title: title ?? "",
    description: description ?? "",
    startDate: formatDateInput(startDate),
    endDate: formatDateInput(endDate),
  };

  // React Hook Form setup
  const form = useForm<z.infer<typeof milestoneSchema>>({
    resolver: zodResolver(milestoneSchema),
    defaultValues: initialValues,
  });

  // When user clicks the edit icon: reset form to current prop values then open editor
  const handleEnterEdit = () => {
    form.reset(initialValues);
    setIsEditing(true);
  };

  // Cancel: revert changes
  // - for existing milestone: revert to prop values and close editor
  // - for new milestone: revert to empty values and stay in editor (no parent to remove this card)
  const handleCancel = () => {
    if (isNew) {
      // This is the temp "new milestone" -> unmount
      onCancel?.();
      return;
    }

    // Existing milestone: revert form and close editor
    form.reset(initialValues);
    setIsEditing(false);
  };

  // Delete existing milestone
  const handleDelete = async () => {
    // If no id, cannot delete
    if (!id) return;
    try {
      const response = await milestoneService.deleteMilestone(id);
      if (response.status === "success") {
        onDeleted?.(id);
        toast.success("Xóa cột mốc thành công");
      } else {
        toast.error(response.message || "Không thể xóa cột mốc");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi xóa cột mốc");
    }
  }

  // On form submit: either create new or update existing
  const onSubmit = async (data: z.infer<typeof milestoneSchema>) => {
    try {
      if (isNew) {
        // Create new milestone via API
        const response = await milestoneService.createMilestone({
          projectId,
          title: data.title,
          description: data.description ?? "",
          startDate: data.startDate,
          endDate: data.endDate,
        });

        if (response.status === "success" && response.data) {
          onCreated?.(response.data);
          toast.success("Tạo cột mốc thành công");
        } else {
          toast.error(response.message || "Không thể tạo cột mốc");
        }
      } else {
        // Update existing milestone via API
        const response = await milestoneService.updateMilestone(id!, {
          title: data.title,
          description: data.description ?? "",
          startDate: data.startDate,
          endDate: data.endDate,
        });

        if (response.status === "success" && response.data) {
          onUpdated?.(response.data);
          toast.success("Cập nhật cột mốc thành công");
          setIsEditing(false);
        } else {
          toast.error(response.message || "Không thể cập nhật cột mốc");
        }
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi lưu cột mốc");
    }
  };
  
  return (
    <Card className="border-border bg-card hover:bg-gray-50 transition-colors h-fit">
      {/* Normal (display) mode: only show when not editing and not creating new */}
      {!isEditing && !isNew && (
        <>
          <CardHeader>
            <CardTitle className="flex flex-col items-start text-lg font-semibold text-foreground">
              <div className="flex items-center justify-between w-full">
                {status === "COMPLETED" ? (
                  <CheckCircle2 className="w-5 h-5 text-success" />
                ) : status === "IN_PROGRESS" ? (
                  <Clock className="w-5 h-5 text-primary" />
                ) : (
                  <Lock className="w-5 h-5 text-red-400" />
                )}

                {userRole == "student" && (
                  <div className="flex items-center gap-2">
                    <Button
                      size="icon"
                      variant="ghost"
                      className="rounded-full cursor-pointer"
                      onClick={handleEnterEdit}
                    >
                      <SquarePen />
                    </Button>

                    <Button
                      size="icon"
                      variant="destructive"
                      className="rounded-full cursor-pointer"
                      onClick={handleDelete}
                    >
                      <Trash />
                    </Button>
                  </div>
                )}

              </div>

              <div
                className="cursor-pointer hover:underline"
                onClick={() =>
                  navigate(`/project/${projectId}/milestone/${id}`)
                }
              >
                {title}
              </div>
            </CardTitle>
            <p className="text-sm text-muted-foreground mt-2">{description}</p>
          </CardHeader>

          <CardContent className="space-y-3">
            <div>
              <span className="text-sm text-muted-foreground">
                {startDate ? startDate.toLocaleDateString() : "—"} -{" "}
                {endDate ? endDate.toLocaleDateString() : "—"}
              </span>
            </div>

            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-sm text-foreground">Tiến độ</span>
                <span className="text-sm font-bold text-primary">
                  {completionPercentage}%
                </span>
              </div>
              <Progress value={completionPercentage} className="h-2" />
            </div>

            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">
                {isLocked
                  ? "Đã khóa"
                  : status === "IN_PROGRESS"
                    ? "Đang tiến hành"
                    : "Hoàn thành"}
              </span>
              <span className="font-semibold text-foreground">
                {tasksCompleted}/{tasksTotal}
              </span>
            </div>
          </CardContent>
        </>
      )}

      {/* Edit mode: used for both update and create */}
      {isEditing && (
        <CardContent className="p-6">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>
                      <Input
                        {...field}
                        placeholder="Mục tiêu"
                        ref={titleRef}
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
                        rows={3}
                        {...field}
                        placeholder="Mô tả"
                        className="bg-white"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <div className="flex flex-row items-center justify-start gap-3">
                <span className="text-sm font-semibold">Thời gian:</span>

                <FormField
                  control={form.control}
                  name="startDate"
                  render={({ field }) => (
                    <FormItem>
                      <FormControl>
                        <Input type="date" {...field} className="bg-white" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                -
                <FormField
                  control={form.control}
                  name="endDate"
                  render={({ field }) => (
                    <FormItem>
                      <FormControl>
                        <Input type="date" {...field} className="bg-white" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <div className="flex gap-2">
                <Button type="submit" className="cursor-pointer">Lưu</Button>
                <Button type="button" variant="ghost" className="cursor-pointer" onClick={handleCancel}>
                  Quay lại
                </Button>
              </div>
            </form>
          </Form>
        </CardContent>
      )}
    </Card>
  );
};
