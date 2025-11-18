// Minimal representation of a milestone data for API calls 

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
