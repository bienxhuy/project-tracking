import { useState, useRef } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Upload } from "lucide-react";

import { Attachment } from "@/types/attachment.type";
import { FileCard } from "@/components/FileCard";
import { updateReport as updateReportService } from "@/services/report.service";

interface ProgressReportEditorProps {
  mode: "create" | "edit";
  reportId?: number; // Required for edit mode
  initialData?: {
    title: string;
    content: string;
    attachments: Attachment[];
  };
  onSuccess?: () => void; // Callback after successful create/update
  onCancel: () => void;
  submitLabel?: string;
  cancelLabel?: string;
}

const reportSchema = z.object({
  title: z.string().min(5, "Tiêu đề phải có ít nhất 5 ký tự"),
  content: z.string().min(10, "Nội dung phải có ít nhất 10 ký tự"),
});

const MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
const ACCEPTED_FILE_TYPES = [
  "application/pdf",
  "application/msword",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  "application/zip",
];

export const ProgressReportEditor = ({
  mode,
  reportId,
  initialData,
  onSuccess,
  onCancel,
  submitLabel = mode === "create" ? "Gửi báo cáo" : "Lưu",
}: ProgressReportEditorProps) => {
  const [attachments, setAttachments] = useState<Attachment[]>(initialData?.attachments || []);
  const [newFiles, setNewFiles] = useState<File[]>([]);
  const [fileError, setFileError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Form setup
  const form = useForm<z.infer<typeof reportSchema>>({
    resolver: zodResolver(reportSchema),
    defaultValues: {
      title: initialData?.title || "",
      content: initialData?.content || "",
    },
  });

  // Helper function to validate file
  const validateFile = (file: File): string | null => {
    if (file.size > MAX_FILE_SIZE) {
      return `Tệp "${file.name}" vượt quá kích thước tối đa 50MB`;
    }
    if (!ACCEPTED_FILE_TYPES.includes(file.type)) {
      return `Tệp "${file.name}" không đúng định dạng. Chỉ chấp nhận PDF, DOC, DOCX, ZIP`;
    }
    return null;
  };

  // Event handler after file selection
  // Add selected files to newFiles state after validation
  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(event.target.files || []);
    setFileError(null);

    for (const file of files) {
      const error = validateFile(file);
      if (error) {
        setFileError(error);
        return;
      }
    }

    setNewFiles((prev) => [...prev, ...files]);
  };

  // Remove a NEWLY added file
  const handleRemoveNewFile = (index: number) => {
    setNewFiles((prev) => prev.filter((_, i) => i !== index));
    setFileError(null);
  };

  // Remove an EXISTING attachment
  const handleRemoveExistingAttachment = (attachmentId: number) => {
    setAttachments((prev) => prev.filter((att) => att.id !== attachmentId));
  };

  // On submit, handle create/update logic
  const handleSubmit = async (data: z.infer<typeof reportSchema>) => {
    setIsSubmitting(true);
    try {
      if (mode === "edit" && reportId) {
        // TODO: Upload new files and get their IDs
        console.log("Files to upload:", newFiles);
        await updateReportService(reportId, { 
          title: data.title, 
          content: data.content,
          // In production, include updated attachment IDs
        });
      } else if (mode === "create") {
        // TODO: Upload files and create report via API
        console.log("Creating report with files:", newFiles);
        console.log("Report data:", { title: data.title, content: data.content, attachments });
      }
      
      // Call success callback to update parent UI
      onSuccess?.();
      
      // Reset form
      form.reset();
      setAttachments([]);
      setNewFiles([]);
      setFileError(null);
    } catch (error) {
      console.error("Failed to submit report:", error);
      setFileError("Không thể lưu báo cáo. Vui lòng thử lại.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // On cancel, reset form and call onCancel callback
  const handleCancel = () => {
    form.reset();
    setAttachments(initialData?.attachments || []);
    setNewFiles([]);
    setFileError(null);
    onCancel();
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              {mode === "create" && <FormLabel>Tiêu đề báo cáo</FormLabel>}
              <FormControl>
                <Input
                  {...field}
                  placeholder={mode === "create" ? "Ví dụ: Báo cáo tiến độ tuần 3" : "Tiêu đề báo cáo"}
                  className={mode === "create" ? "bg-white" : "font-semibold text-lg bg-white"}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="content"
          render={({ field }) => (
            <FormItem>
              {mode === "create" && <FormLabel>Nội dung</FormLabel>}
              <FormControl>
                <Textarea
                  {...field}
                  placeholder={
                    mode === "create"
                      ? "Mô tả chi tiết những gì đã hoàn thành, vấn đề gặp phải và kế hoạch tiếp theo..."
                      : "Nội dung báo cáo"
                  }
                  rows={mode === "create" ? 5 : 4}
                  className="bg-white"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Attachments Section */}
        <div className="space-y-2">
          <label className="text-sm font-medium">
            {mode === "create" ? "Tệp đính kèm (Tùy chọn)" : "Tệp đính kèm"}
          </label>

          {/* Existing Attachments */}
          {attachments.length > 0 && (
            <div className="space-y-2">
              <p className="text-xs text-muted-foreground">Tệp hiện có:</p>
              {attachments.map((attachment) => (
                <FileCard
                  key={attachment.id}
                  fileName={attachment.originalFilename}
                  fileSize={attachment.fileSize}
                  downloadUrl={attachment.storageUrl}
                  onRemove={() => handleRemoveExistingAttachment(attachment.id)}
                  variant="existing"
                />
              ))}
            </div>
          )}

          {/* New Files */}
          {newFiles.length > 0 && (
            <div className="space-y-2">
              <p className="text-xs text-muted-foreground">Tệp mới:</p>
              {newFiles.map((file, index) => (
                <FileCard
                  key={index}
                  fileName={file.name}
                  fileSize={file.size}
                  onRemove={() => handleRemoveNewFile(index)}
                  variant="new"
                />
              ))}
            </div>
          )}

          {/* File Upload Button */}
          <div className="flex gap-2 items-center">
            <Button
              type="button"
              variant="outline"
              size="sm"
              className="cursor-pointer"
              onClick={() => fileInputRef.current?.click()}
            >
              <Upload className="w-4 h-4 mr-2" />
              {mode === "create" ? "Chọn tệp" : "Thêm tệp"}
            </Button>
            <input
              ref={fileInputRef}
              type="file"
              multiple
              accept=".pdf,.doc,.docx,.zip"
              onChange={handleFileSelect}
              className="hidden"
            />
            <span className="text-xs text-muted-foreground">
              PDF, DOC, DOCX, ZIP (Tối đa 50MB/tệp)
            </span>
          </div>

          {fileError && (
            <p className="text-sm text-destructive">{fileError}</p>
          )}
        </div>

        <div className="flex gap-2">
          <Button 
            type="submit" 
            size={mode === "edit" ? "sm" : undefined} 
            className="cursor-pointer"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Đang lưu..." : submitLabel}
          </Button>
          <Button
            type="button"
            size={mode === "edit" ? "sm" : undefined}
            variant="ghost"
            className="cursor-pointer"
            onClick={handleCancel}
            disabled={isSubmitting}
          >
            Hủy
          </Button>
        </div>
      </form>
    </Form>
  );
};
