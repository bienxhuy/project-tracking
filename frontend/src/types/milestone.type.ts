// Minimal representation of a milestone data for API calls 

export interface Milestone {
  id: number;
  title: string;
  description: string;
  startDate: Date;
  endDate: Date;
  status: "LOCKED" | "IN_PROGRESS" | "COMPLETED";
  orderNumber: number;
  completionPercentage: number;
  tasksTotal: number;
  tasksCompleted: number;
}
