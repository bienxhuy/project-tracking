// Minimal representation of a task data for API calls

import { Report } from "./report.type";

export interface Task {
  id: number;
  title: string;
  description: string;
  startDate: Date;
  endDate: Date;
  status: "IN_PROGRESS" | "COMPLETED";
  isLocked: boolean;
  assignees: Array<{ id: number; name: string; initials: string }>;
}

export interface TaskDetail extends Task {
  reports: Array<Report>;
}
