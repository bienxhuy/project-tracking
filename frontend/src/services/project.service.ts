// API Service to fetch project data

import { currentBatchProjects, projectDetailDummy } from "@/data/dummy/projects.dummy";

export function fetchTempProjects() {
  // TODO: Replace with real API call
  return currentBatchProjects;
}

export function fetchDetailProject() {
  // TODO: Replace with real API call
  return projectDetailDummy;
}
