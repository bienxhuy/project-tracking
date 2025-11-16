// Minimal representation of a task data for API calls

export interface Task {
  id: string;
  title: string;
  description: string;
  assignees: Array<{ id: string; name: string; initials: string }>;
  startDate: string;
  endDate: string;
  completed: boolean;
  isLocked: boolean;
}
