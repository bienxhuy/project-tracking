// Simulated data from BE

import { Milestone } from "@/types/milestone.type";

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
