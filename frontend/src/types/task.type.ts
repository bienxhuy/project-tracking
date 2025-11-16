// Minimal representation of a task data for API calls

export interface Task {
  id: number;
  title: string;
  description: string;
  assignees: Array<{ id: number; name: string; initials: string }>;
  startDate: Date;
  endDate: Date;
  completed: boolean;
  isLocked: boolean;
}
