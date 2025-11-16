// Minimal representation of a milestone data for API calls 

export interface Milestone {
  id: string;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  status: "LOCKED" | "IN_PROGRESS" | "COMPLETED";
  orderNumber: number;
  completionPercentage: number;
  tasksTotal: number;
  tasksCompleted: number;
}
