// Minimal representation of a Project entity for API Calls

import { Milestone } from "./milestone.type";
import { User } from "./user.type";

export interface Project {
  id: number;
  title: string;
  objective: string;
  content: string;
  year: number;
  semester: number;
  batch: number;
  falculty: string;
  startDate: Date;
  endDate: Date;
  milestoneCount: number;
  memberCount: number;
  completionPercentage: number;
  status: "ACTIVE" | "COMPLETED";
  isLocked: boolean;
}

export interface ProjectDetail extends Project {
  isObjDesLocked: boolean;
  milestones: Milestone[];
  students: User[];
}

export const statusConfig = {
  ACTIVE: { label: "Đang hoạt động", className: "bg-warning text-warning-foreground" },
  COMPLETED: { label: "Hoàn thành", className: "bg-success text-success-foreground" },
};

export interface CreateProjectRequest {
  title: string;
  objective: string;
  content: string;
  year: number;
  semester: number;
  batch: number;
  falculty: string;
  studentIds: number[];
}

export interface UpdateProjectRequest extends CreateProjectRequest {
  id: number;
}
