// API Service to fetch milestone data

import { milestones, milestoneDetail } from "@/data/dummy/milestones.dummy";

export function fetchTempMilestones() {
  return milestones;
}

export function fetchTempMilestoneDetail() {
  return milestoneDetail
}
