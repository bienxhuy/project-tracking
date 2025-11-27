import { ActivityLogEntry } from "@/types/activity.type";

const MOCK_ACTIVITY_LOG: ActivityLogEntry[] = [
  {
    id: 1,
    projectId: 1,
    projectName: "GreenCampus Energy Monitor",
    type: "Progress Report",
    timestamp: new Date().toISOString(),
    owner: "Dr. Alvarez",
  },
  {
    id: 2,
    projectId: 2,
    projectName: "DormMate — Roommate Matching",
    type: "Milestone Update",
    timestamp: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
    owner: "Prof. Tan",
  },
  {
    id: 3,
    projectId: 3,
    projectName: "Lab Scheduler & Resource Booker",
    type: "Progress Report",
    timestamp: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
    owner: "Ms. Rivera",
  },
  {
    id: 4,
    projectId: 4,
    projectName: "VR Campus Tour",
    type: "New Project",
    timestamp: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000).toISOString(),
    owner: "Dr. Singh",
  },
];

class ActivityService {
  async getRecentActivity(): Promise<ActivityLogEntry[]> {
    // TODO: Replace with real API call when backend endpoint is available
    return Promise.resolve(MOCK_ACTIVITY_LOG);
  }
}

export const activityService = new ActivityService();

