// Simulated data from BE

import { Notification } from "@/types/notification.type";

export const notifications: Notification[] = [
  {
    id: 1,
    title: "Bình luận mới được thêm",
    message: "Alice đã bình luận trên Nhiệm vụ #12: “Vui lòng tái cấu trúc lớp dịch vụ API.”",
    isRead: false,
    createdDate: new Date("2025-11-16T10:20:00Z"),
    link: "/projects/1/tasks/12",
    project: { 
      id: 1,
      title: "Thiết kế lại Website",
    },
  },
  {
    id: 2,
    title: "Cột mốc đã hoàn thành",
    message: "Cột mốc 2: Thành phần giao diện người dùng đã được đánh dấu là hoàn thành.",
    isRead: true,
    createdDate: new Date("2025-11-14T08:45:00Z"),
    link: "/projects/1/milestones/2",
    project: {
      id: 1,
      title: "Thiết kế lại Website",
    },
  },
  {
    id: 3,
    title: "Nhiệm vụ được giao cho bạn",
    message: "Bạn đã được giao Nhiệm vụ #88: Triển khai luồng đăng nhập.",
    isRead: false,
    createdDate: new Date("2025-11-15T07:15:00Z"),
    link: "/projects/2/tasks/88",
    project: {
      id: 2,
      title: "Ứng dụng di động - Cổng thông tin sinh viên",
    },
  },
  {
    id: 4,
    title: "Yêu cầu đánh giá PR",
    message: "Một yêu cầu đánh giá đã được gửi cho Pull Request #45.",
    isRead: false,
    createdDate: new Date("2025-11-16T06:30:00Z"),
    link: "/projects/2/pull-requests/45",
    project: {
      id: 2,
      title: "Ứng dụng di động - Cổng thông tin sinh viên",
    },
  },
  {
    id: 5,
    title: "Thành viên mới đã tham gia",
    message: "Tiến sĩ Nguyễn đã tham gia dự án với vai trò là người đánh giá.",
    isRead: true,
    createdDate: new Date("2025-11-10T10:20:00Z"),
    link: "/projects/3/members",
    project: {
      id: 3,
      title: "Trợ lý lập lịch AI",
    },
  },
];
