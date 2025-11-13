import { MilestoneCard } from "@/types/milestoneCard.type";

export const milestones: MilestoneCard[] = [
  {
    id: "m1",
    projectId: "1",
    title: "Project Planning",
    description: "Define requirements, create wireframes, and set up development environment",
    tasksTotal: 5,
    tasksCompleted: 5,
    completed: true,
    progress: 100,
  },
  {
    id: "m2",
    projectId: "1",
    title: "Frontend Development",
    description: "Build responsive UI components and integrate with backend APIs",
    tasksTotal: 12,
    tasksCompleted: 10,
    completed: false,
    progress: 83,
  }
];
