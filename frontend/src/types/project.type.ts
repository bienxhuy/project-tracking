// Minimal representation of a Project entity for API Calls

import { Milestone } from "./milestone.type";

export interface Project {
  id: string;
  title: string;
  content: string;
  year: string;
  semester: string;
  batch: string;
  falculty: string;
  startDate: string;
  endDate: string;
  milestoneCount: number;
  memberCount: number;
  completionPercentage: number;
  status: "active" | "completed" | "locked";
}

export interface ProjectDetail extends Project {
  objective: string;
  milestones: Milestone[];
}
