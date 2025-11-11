import { ProjectCard } from "@/types/projectCard.type";

export const currentBatchProjects: ProjectCard[] = [
  {
    id: "1",
    title: "Student Project Progress Tracking System",
    semester: "Semester 1",
    year: "2025",
    batch: "1",
    progress: 68,
    members: 3,
    milestones: 5,
    completedMilestones: 3,
    status: "active" as const,
  },
  {
    id: "2",
    title: "E-Learning Platform Development",
    semester: "Semester 1",
    year: "2025",
    batch: "2",
    progress: 45,
    members: 4,
    milestones: 4,
    completedMilestones: 1,
    status: "active" as const
  },
];