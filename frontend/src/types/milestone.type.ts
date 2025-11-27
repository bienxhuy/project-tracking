// Minimal representation of a milestone data for API calls 
import { Task } from "./task.type";

export interface Milestone {
  id: number;
  title: string;
  description: string;
  startDate: Date;
  endDate: Date;
  status: "IN_PROGRESS" | "COMPLETED";
  isLocked: boolean;
  orderNumber: number;
  completionPercentage: number;
  tasksTotal: number;
  tasksCompleted: number;
}

export interface MilestoneDetail extends Milestone {
  tasks: Task[];
}
