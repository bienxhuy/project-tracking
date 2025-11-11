export interface ProjectCard {
  id: string;
  title: string;
  semester: string;
  year: string;
  batch: string;
  progress: number;
  members: number;
  milestones: number;
  completedMilestones: number;
  status: "active" | "completed" | "pending";
}
