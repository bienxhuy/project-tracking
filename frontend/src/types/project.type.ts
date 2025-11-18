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
  status: "ACTIVE" | "COMPLETED" | "LOCKED";
}

export interface ProjectDetail extends Project {
  objective: string;
  milestones: Milestone[];
}
