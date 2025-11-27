export type ActivityLogType = "Progress Report" | "Milestone Update" | "New Project";

export interface ActivityLogEntry {
  id: number;
  projectId: number;
  projectName: string;
  type: ActivityLogType;
  timestamp: string;
  owner: string;
}

