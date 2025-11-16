import { Milestone } from "@/types/milestone.type";

export const milestones: Milestone[] = [
  {
    id: "m1",
    title: "Project Planning",
    description: "Define requirements, create wireframes, and set up development environment",
    tasksTotal: 5,
    tasksCompleted: 5,
    completionPercentage: 100,
    startDate: "",
    endDate: "",
    status: "IN_PROGRESS",
    orderNumber: 0
  },
  {
    id: "m2",
    title: "Frontend Development",
    description: "Build responsive UI components and integrate with backend APIs",
    tasksTotal: 12,
    tasksCompleted: 10,
    completionPercentage: 83,
    startDate: "",
    endDate: "",
    status: "LOCKED",
    orderNumber: 1
  }
];
