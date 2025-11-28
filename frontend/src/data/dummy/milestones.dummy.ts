// Simulated data from BE

import { Milestone, MilestoneDetail } from "@/types/milestone.type";

export const milestones: Milestone[] = [
  {
    id: 1,
    title: "Lập kế hoạch dự án",
    description: "Xác định yêu cầu, tạo wireframe và thiết lập môi trường phát triển",
    tasksTotal: 5,
    tasksCompleted: 5,
    completionPercentage: 100,
    startDate: new Date("2025-01-01T00:00:00Z"),
    endDate: new Date("2025-01-10T00:00:00Z"),
    status: "IN_PROGRESS",
    isLocked: false,
    orderNumber: 0,
  },
  {
    id: 2,
    title: "Phát triển giao diện người dùng",
    description: "Xây dựng các thành phần giao diện người dùng đáp ứng và tích hợp với API backend",
    tasksTotal: 12,
    tasksCompleted: 10,
    completionPercentage: 83,
    startDate: new Date("2025-01-11T00:00:00Z"),
    endDate: new Date("2025-02-05T00:00:00Z"),
    status: "IN_PROGRESS",
    isLocked: true,
    orderNumber: 1,
  },
];

export const milestoneDetail: MilestoneDetail = {
  id: 1,
  title: "Hoàn thiện tài liệu yêu cầu",
  description: "Thu thập yêu cầu từ khách hàng, phân tích và lập tài liệu mô tả chức năng hệ thống.",
  startDate: new Date("2025-01-10"),
  endDate: new Date("2025-01-25"),
  status: "IN_PROGRESS",
  isLocked: false,
  orderNumber: 1,
  completionPercentage: 40,
  tasksTotal: 5,
  tasksCompleted: 2,

  tasks: [
    {
      id: 101,
      title: "Phỏng vấn khách hàng",
      description: "Làm việc trực tiếp để nắm yêu cầu thực tế.",
      startDate: new Date("2025-01-10"),
      endDate: new Date("2025-01-12"),
      status: "COMPLETED",
      isLocked: false,
      assignees: [
        { id: 1, name: "Nguyễn Văn A", initials: "NA" }
      ]
    },
    {
      id: 102,
      title: "Xác định use case",
      description: "Phân tích các luồng sử dụng của hệ thống.",
      startDate: new Date("2025-01-13"),
      endDate: new Date("2025-01-15"),
      status: "IN_PROGRESS",
      isLocked: false,
      assignees: [
        { id: 2, name: "Trần Thị B", initials: "TB" }
      ]
    },
    {
      id: 103,
      title: "Thiết kế mô hình dữ liệu",
      description: "Tạo ERD và mô tả bảng dữ liệu.",
      startDate: new Date("2025-01-15"),
      endDate: new Date("2025-01-18"),
      status: "IN_PROGRESS",
      isLocked: false,
      assignees: [
        { id: 1, name: "Nguyễn Văn A", initials: "NA" }
      ]
    },
    {
      id: 104,
      title: "Viết tài liệu đặc tả",
      description: "Mô tả chi tiết từng chức năng.",
      startDate: new Date("2025-01-18"),
      endDate: new Date("2025-01-23"),
      status: "IN_PROGRESS",
      isLocked: false,
      assignees: [
        { id: 3, name: "Lê Văn C", initials: "LC" }
      ]
    },
    {
      id: 105,
      title: "Xác nhận với khách hàng",
      description: "Trình bày tài liệu và nhận phản hồi cuối cùng.",
      startDate: new Date("2025-01-23"),
      endDate: new Date("2025-01-25"),
      status: "IN_PROGRESS",
      isLocked: true,
      assignees: [
        { id: 2, name: "Trần Thị B", initials: "TB" },
        { id: 3, name: "Lê Văn C", initials: "LC" }
      ]
    }
  ]
};
