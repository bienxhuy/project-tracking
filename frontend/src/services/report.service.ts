// API service for reports

import { ReportDetail, Report } from "@/types/report.type";
import { Attachment } from "@/types/attachment.type";

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

export const createReport = async (
  taskId: number,
  data: { title: string; content: string; files: File[] }
): Promise<Report> => {
  // TODO: Replace with actual API call
  // 1. Upload files to backend
  // 2. Backend handles file upload to cloud storage
  // 3. Backend creates report with file references
  // 4. Returns created report with ID and attachment info
  
  await new Promise(resolve => setTimeout(resolve, 500));
  
  // Mock response - in production, this would be the actual report from backend
  const mockAttachments: Attachment[] = data.files.map((file, index) => ({
    id: Date.now() + index,
    originalFilename: file.name,
    storedFilename: `${Date.now()}_${file.name}`,
    fileSize: file.size,
    fileType: file.type,
    storageUrl: `https://storage.example.com/reports/${Date.now()}_${file.name}`,
  }));
  
  return {
    id: Date.now(), // Backend will generate real ID
    title: data.title,
    content: data.content,
    attachments: mockAttachments,
    status: "SUBMITTED",
  };
};

export const updateReport = async (
  reportId: number,
  data: { 
    title: string; 
    content: string; 
    files: File[]; 
    existingAttachmentIds: number[];
    removedAttachmentIds: number[];
  }
): Promise<Report> => {
  // TODO: Replace with actual API call
  // 1. Upload new files to backend first
  // 2. Backend handles file upload to cloud storage and returns file IDs
  // 3. Send update request with: new title, new content, new file IDs, existing file IDs, removed file IDs
  // 4. Backend processes the update and returns complete updated report
  
  await new Promise(resolve => setTimeout(resolve, 500));
  
  // Mock: Upload new files and get their info
  const mockNewAttachments: Attachment[] = data.files.map((file, index) => ({
    id: Date.now() + index,
    originalFilename: file.name,
    storedFilename: `${Date.now()}_${file.name}`,
    fileSize: file.size,
    fileType: file.type,
    storageUrl: `https://storage.example.com/reports/${Date.now()}_${file.name}`,
  }));
  
  // Mock: Keep only existing attachments that weren't removed
  const mockExistingAttachments: Attachment[] = data.existingAttachmentIds
    .filter(id => !data.removedAttachmentIds.includes(id))
    .map(id => ({
      id,
      originalFilename: `existing_file_${id}.pdf`,
      storedFilename: `stored_${id}.pdf`,
      fileSize: 100000,
      fileType: "application/pdf",
      storageUrl: `https://storage.example.com/reports/existing_${id}.pdf`,
    }));
  
  return {
    id: reportId,
    title: data.title,
    content: data.content,
    attachments: [...mockExistingAttachments, ...mockNewAttachments],
    status: "SUBMITTED",
  };
};

export const toggleReportLock = async (_reportId: number): Promise<void> => {
  // TODO: Replace with actual API call
  await new Promise(resolve => setTimeout(resolve, 0));
};
