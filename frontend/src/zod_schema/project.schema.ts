import { z } from "zod";

// Project schema for creating/editing projects
export const projectSchema = z.object({
  title: z.string().trim().min(3, { message: "Tên dự án phải có ít nhất 3 ký tự." }),
  objectives: z.string().trim().min(10, { message: "Mục tiêu phải có ít nhất 10 ký tự." }).max(1000, { message: "Mục tiêu không được quá 1000 ký tự." }),
  content: z.string().trim().min(10, { message: "Mô tả phải có ít nhất 10 ký tự." }).max(2000, { message: "Mô tả không được quá 2000 ký tự." }),
  year: z.number().min(2000, { message: "Năm học phải từ 2000 trở lên." }),
  semester: z.number().min(1).max(3, { message: "Học kỳ phải từ 1 đến 3." }),
  batch: z.number().min(1).max(3, { message: "Đợt phải từ 1 đến 3." }),
  falculty: z.string().trim().min(2, { message: "Khoa phải có ít nhất 2 ký tự." }),
  startDate: z.date({ message: "Ngày bắt đầu là bắt buộc." }),
  endDate: z.date({ message: "Ngày kết thúc là bắt buộc." }),
  studentIds: z.array(z.number()).min(1, { message: "Phải có ít nhất một sinh viên tham gia." }),
}).refine((data) => data.endDate > data.startDate, {
  message: "Ngày kết thúc phải sau ngày bắt đầu.",
  path: ["endDate"],
});
