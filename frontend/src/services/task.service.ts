// API service for tasks

import { tasks } from "@/data/dummy/task.dummy";
import { taskDetail } from "@/data/dummy/task-detail.dummy";
import { TaskDetail } from "@/types/task.type";

export function fetchTempTasks() {
  return tasks;
}

export const fetchTaskDetail = (): TaskDetail => {
  // TODO: Replace with actual API call
  return taskDetail;
};
