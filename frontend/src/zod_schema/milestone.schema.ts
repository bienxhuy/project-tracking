import { z } from "zod";

// Milstone schema for editing milestone
export const milestoneSchema = z.object({
  title: z.string().trim().min(3, { message: "Tên cột mốc phải có ít nhất 3 ký tự." }),
   description: z.string().max(1000, "Mô tả không được quá 1000 ký tự"),
  startDate: z.string().refine(
    (v) => !isNaN(new Date(v).getTime()),
    { message: "Ngày bắt đầu không hợp lệ." }
  ),
  endDate: z.string().refine(
    (v) => !isNaN(new Date(v).getTime()),
    { message: "Ngày kết thúc không hợp lệ." }
  )
}).refine(
  (data) => new Date(data.startDate) <= new Date(data.endDate),
  { message: "Ngày bắt đầu phải sau ngày kết thúc.", path: ["endDate"] }
);
