// API service for reports

import { ReportDetail } from "@/types/report.type";

export const fetchReportDetail = async (reportId: number): Promise<ReportDetail> => {
  // TODO: Replace with actual API call
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 500));
  
  // Mock data - in production, this would be an API call
  return {
    id: reportId,
    title: "Báo cáo tiến độ",
    content: "Nội dung báo cáo chi tiết",
    attachments: [],
    status: "SUBMITTED",
    comments: [
      {
        id: 1,
        content: "Làm tốt lắm! Hãy chắc chắn thêm index cho các trường được truy vấn thường xuyên nhé.",
        createdDate: new Date("2024-03-10T15:00:00"),
        commenter: { id: 10, name: "GV. Lê Văn Giảng", initials: "LG" },
      },
      {
        id: 2,
        content: "Cảm ơn thầy! Em sẽ bổ sung thêm.",
        createdDate: new Date("2024-03-10T16:30:00"),
        commenter: { id: 1, name: "Nguyễn Văn A", initials: "NA" },
      },
    ],
  };
};

export const addCommentToReport = async (
  reportId: number,
  content: string,
  mentions: number[]
): Promise<{ id: number; content: string; createdDate: Date; commenter: { id: number; name: string; initials: string } }> => {
  // TODO: Replace with actual API call
  await new Promise(resolve => setTimeout(resolve, 0));
  
  return {
    id: Date.now(),
    content,
    createdDate: new Date(),
    commenter: { id: 1, name: "Current User", initials: "CU" },
  };
};

export const deleteCommentFromReport = async (
  reportId: number,
  commentId: number
): Promise<void> => {
  // TODO: Replace with actual API call
  await new Promise(resolve => setTimeout(resolve, 0));
};

export const updateReport = async (
  reportId: number,
  data: { title: string; content: string }
): Promise<void> => {
  // TODO: Replace with actual API call
  await new Promise(resolve => setTimeout(resolve, 0));
};

export const deleteReport = async (reportId: number): Promise<void> => {
  // TODO: Replace with actual API call
  await new Promise(resolve => setTimeout(resolve, 0));
};

export const toggleReportLock = async (reportId: number): Promise<void> => {
  // TODO: Replace with actual API call
  await new Promise(resolve => setTimeout(resolve, 0));
};
