import { z } from "zod";

// Task schema for creating/editing tasks
export const taskSchema = z.object({
  title: z.string().trim().min(3, { message: "Tiêu đề phải có ít nhất 3 ký tự." }),
  description: z.string().min(10, { message: "Mô tả phải có ít nhất 10 ký tự." }).max(1000, { message: "Mô tả không được quá 1000 ký tự." }),
  startDate: z.string().refine(
    (v) => !isNaN(new Date(v).getTime()),
    { message: "Ngày bắt đầu không hợp lệ." }
  ),
  endDate: z.string().refine(
    (v) => !isNaN(new Date(v).getTime()),
    { message: "Ngày kết thúc không hợp lệ." }
  ),
  assigneeIds: z.array(z.number()).min(1, { message: "Phải có ít nhất một người đảm nhiệm." }),
}).refine(
  (data) => new Date(data.startDate) <= new Date(data.endDate),
  { message: "Ngày bắt đầu phải trước ngày kết thúc.", path: ["endDate"] }
);
