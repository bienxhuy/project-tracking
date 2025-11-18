// Minimal representation of a task data for API calls

export interface Task {
  id: number;
  title: string;
  description: string;
  startDate: Date;
  endDate: Date;
  status: "IN_PROGRESS" | "COMPLETED" | "LOCKED";
  assignees: Array<{ id: number; name: string; initials: string }>;
}
