// Simulated data from BE

import { Milestone } from "@/types/milestone.type";

export const milestones: Milestone[] = [
  {
    id: 1,
    title: "Project Planning",
    description: "Define requirements, create wireframes, and set up development environment",
    tasksTotal: 5,
    tasksCompleted: 5,
    completionPercentage: 100,
    startDate: new Date("2025-01-01T00:00:00Z"),
    endDate: new Date("2025-01-10T00:00:00Z"),
    status: "IN_PROGRESS",
    orderNumber: 0,
  },
  {
    id: 2,
    title: "Frontend Development",
    description: "Build responsive UI components and integrate with backend APIs",
    tasksTotal: 12,
    tasksCompleted: 10,
    completionPercentage: 83,
    startDate: new Date("2025-01-11T00:00:00Z"),
    endDate: new Date("2025-02-05T00:00:00Z"),
    status: "LOCKED",
    orderNumber: 1,
  },
];
