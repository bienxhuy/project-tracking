// Simulated TaskDetail data from BE

import { TaskDetail } from "@/types/task.type";
import { BaseUser } from "@/types/user.type";

export const taskDetail: TaskDetail = {
  id: 1,
  title: "Thiết kế cơ sở dữ liệu",
  description: "Tạo và tối ưu hóa schema cơ sở dữ liệu cho hệ thống quản lý dự án, bao gồm các bảng: projects, milestones, tasks, users, reports, comments và các mối quan hệ giữa chúng.",
  assignees: [
    { id: 1, displayName: "Nguyễn Văn A", email: "nguyenvana@example.com", role: "STUDENT" },
    { id: 2, displayName: "Trần Thị B", email: "tranthib@example.com", role: "STUDENT" },
  ],
  startDate: new Date("2024-03-01"),
  endDate: new Date("2024-03-15"),
  status: "IN_PROGRESS",
  isLocked: false,
  reports: [
    {
      id: 1,
      title: "Báo cáo tiến độ tuần 1",
      content: "Đã hoàn thành thiết kế schema cho các bảng users và projects. Thêm các ràng buộc khóa ngoại và ràng buộc cơ bản. Đang nghiên cứu cách tối ưu hóa query cho các trường được truy vấn thường xuyên.",
      attachments: [
        {
          id: 1,
          originalFilename: "database-schema-v1.pdf",
          storedFilename: "db-schema-20240301.pdf",
          fileSize: 245678,
          fileType: "application/pdf",
          storageUrl: "/uploads/db-schema-20240301.pdf",
        },
        {
          id: 2,
          originalFilename: "erd-diagram.png",
          storedFilename: "erd-20240301.png",
          fileSize: 128934,
          fileType: "image/png",
          storageUrl: "/uploads/erd-20240301.png",
        },
      ],
      status: "SUBMITTED",
    },
    {
      id: 2,
      title: "Báo cáo tiến độ tuần 2",
      content: "Đang làm việc trên các bảng milestone và task. Triển khai mối quan hệ nhiều-nhiều cho việc phân công công việc. Đã thêm các trigger để tự động cập nhật trạng thái milestone khi task thay đổi.",
      attachments: [],
      status: "SUBMITTED",
    },
  ],
};

// Available project members for tagging in comments
export const projectMembers: BaseUser[] = [
  { id: 1, displayName: "Nguyễn Văn A", email: "nguyenvana@example.com", role: "STUDENT" },
  { id: 2, displayName: "Trần Thị B", email: "tranthib@example.com", role: "STUDENT" },
  { id: 3, displayName: "Lê Văn C", email: "levanc@example.com", role: "STUDENT" },
  { id: 4, displayName: "Phạm Thị D", email: "phamthid@example.com", role: "STUDENT" },
  { id: 10, displayName: "GV. Lê Văn Giảng", email: "levangiang@example.com", role: "INSTRUCTOR" },
];
