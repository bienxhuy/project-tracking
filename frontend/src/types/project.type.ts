// Minimal representation of a Project entity for API Calls

import { Milestone } from "./milestone.type";

export interface Project {
  id: number;
  title: string;
  content: string;
  year: number;
  semester: number;
  batch: string;
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
  objective: string;
  milestones: Milestone[];
}

export const statusConfig = {
  ACTIVE: { label: "Đang hoạt động", className: "bg-warning text-warning-foreground" },
  COMPLETED: { label: "Hoàn thành", className: "bg-success text-success-foreground" },
};
