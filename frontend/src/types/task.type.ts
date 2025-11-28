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

export interface CreateTaskRequest {
  title: string;
  description: string;
  startDate: string; // ISO date format
  endDate: string;
  assigneeIds: number[];
}

export interface UpdateTaskRequest {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  assigneeIds: number[];
}

export interface ToggleTaskStatusRequest {
  status: "IN_PROGRESS" | "COMPLETED";
}

export interface ToggleTaskLockRequest {
  isLocked: boolean;
}
