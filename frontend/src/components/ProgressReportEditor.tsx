import { useState, useRef } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Upload, X, FileText } from "lucide-react";

import { Attachment } from "@/types/attachment.type";

interface ProgressReportEditorProps {
  mode: "create" | "edit";
  initialData?: {
    title: string;
    content: string;
    attachments: Attachment[];
  };
  onSubmit: (data: { title: string; content: string; attachments: Attachment[]; newFiles: File[] }) => void;
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
  initialData,
  onSubmit,
  onCancel,
  submitLabel = mode === "create" ? "Gửi báo cáo" : "Lưu",
}: ProgressReportEditorProps) => {
  const [attachments, setAttachments] = useState<Attachment[]>(initialData?.attachments || []);
  const [newFiles, setNewFiles] = useState<File[]>([]);
  const [fileError, setFileError] = useState<string | null>(null);
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

  // Format file size for display
  const formatFileSize = (bytes: number): string => {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
    return (bytes / (1024 * 1024)).toFixed(1) + " MB";
  };

  // On submit, gather data and call onSubmit callback
  const handleSubmit = (data: z.infer<typeof reportSchema>) => {
    onSubmit({
      title: data.title,
      content: data.content,
      attachments,
      newFiles,
    });
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
                <div
                  key={attachment.id}
                  className="flex items-center justify-between p-2 bg-muted rounded-md"
                >
                  <div className="flex items-center gap-2">
                    <FileText className="w-4 h-4 text-muted-foreground" />
                    <span className="text-sm">{attachment.originalFilename}</span>
                    <span className="text-xs text-muted-foreground">
                      ({formatFileSize(attachment.fileSize)})
                    </span>
                  </div>
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    onClick={() => handleRemoveExistingAttachment(attachment.id)}
                  >
                    <X className="w-4 h-4" />
                  </Button>
                </div>
              ))}
            </div>
          )}

          {/* New Files */}
          {newFiles.length > 0 && (
            <div className="space-y-2">
              <p className="text-xs text-muted-foreground">Tệp mới:</p>
              {newFiles.map((file, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between p-2 bg-primary/5 rounded-md"
                >
                  <div className="flex items-center gap-2">
                    <FileText className="w-4 h-4 text-muted-foreground" />
                    <span className="text-sm">{file.name}</span>
                    <span className="text-xs text-muted-foreground">
                      ({formatFileSize(file.size)})
                    </span>
                  </div>
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    onClick={() => handleRemoveNewFile(index)}
                  >
                    <X className="w-4 h-4" />
                  </Button>
                </div>
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
          <Button type="submit" size={mode === "edit" ? "sm" : undefined} className="cursor-pointer">
            {submitLabel}
          </Button>
          <Button
            type="button"
            size={mode === "edit" ? "sm" : undefined}
            variant="ghost"
            className="cursor-pointer"
            onClick={handleCancel}
          >
            Hủy
          </Button>
        </div>
      </form>
    </Form>
  );
};
