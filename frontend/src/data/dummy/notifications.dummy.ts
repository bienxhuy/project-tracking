// Simulated data from BE

import { Notification } from '@/types/notification.type'

export const notifications: Notification[] = [
  {
    id: 1,
    title: "New Comment Added",
    message: "Alice commented on Task #12: “Please refactor the API service layer.”",
    isRead: false,
    createdDate: new Date("2025-11-16T10:20:00Z"),
    link: "/projects/1/tasks/12",
    project: {
      id: 1,
      title: "Website Redesign",
    },
  },
  {
    id: 2,
    title: "Milestone Completed",
    message: "Milestone 2: UI Components has been marked as completed.",
    isRead: true,
    createdDate: new Date("2025-11-14T08:45:00Z"),
    link: "/projects/1/milestones/2",
    project: {
      id: 1,
      title: "Website Redesign",
    },
  },
  {
    id: 3,
    title: "Task Assigned to You",
    message: "You were assigned Task #88: Implement Login Flow.",
    isRead: false,
    createdDate: new Date("2025-11-15T07:15:00Z"),
    link: "/projects/2/tasks/88",
    project: {
      id: 2,
      title: "Mobile App - Student Portal",
    },
  },
  {
    id: 4,
    title: "PR Review Requested",
    message: "A review was requested for Pull Request #45.",
    isRead: false,
    createdDate: new Date("2025-11-16T06:30:00Z"),
    link: "/projects/2/pull-requests/45",
    project: {
      id: 2,
      title: "Mobile App - Student Portal",
    },
  },
  {
    id: 5,
    title: "New Member Joined",
    message: "Dr. Nguyen has joined the project as a reviewer.",
    isRead: true,
    createdDate: new Date("2025-11-10T10:20:00Z"),
    link: "/projects/3/members",
    project: {
      id: 3,
      title: "AI Scheduling Assistant",
    },
  },
];
