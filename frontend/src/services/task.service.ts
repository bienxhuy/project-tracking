// API service for tasks

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import {
  Task,
  TaskDetail,
  CreateTaskRequest,
  UpdateTaskRequest,
  ToggleTaskLockRequest,
} from "@/types/task.type";

/**
 * Helper function to parse date strings to Date objects in Task
 */
function parseTaskDates<T extends Task | TaskDetail>(task: any): T {
  return {
    ...task,
    startDate: task.startDate ? new Date(task.startDate) : undefined,
    endDate: task.endDate ? new Date(task.endDate) : undefined,
    reports: task.reports?.map((report: any) => ({
      ...report,
      submittedAt: new Date(report.submittedAt),
    })),
  };
}

class TaskService {
  /**
   * Get all tasks for a milestone
   */
  async getTasksByMilestone(milestoneId: number): Promise<ApiResponse<Task[]>> {
    const response = await apiClient.get<ApiResponse<Task[]>>(
      `/api/v1/milestones/${milestoneId}/tasks`
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = response.data.data.map(task => parseTaskDates<Task>(task));
    }
    return response.data;
  }

  /**
   * Get task detail by ID
   * @param taskId - The ID of the task
   * @param includeReports - Whether to include reports array in the task
   */
  async getTaskById(
    taskId: number,
    includeReports?: boolean
  ): Promise<ApiResponse<Task | TaskDetail>> {
    const params = includeReports ? { include: 'reports' } : undefined;
    const response = await apiClient.get<ApiResponse<Task | TaskDetail>>(
      `/api/v1/tasks/${taskId}`,
      { params }
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = includeReports
        ? parseTaskDates<TaskDetail>(response.data.data)
        : parseTaskDates<Task>(response.data.data);
    }
    return response.data;
  }

  /**
   * Create a new task in a milestone (Student)
   */
  async createTask(
    data: CreateTaskRequest
  ): Promise<ApiResponse<Task>> {
    const response = await apiClient.post<ApiResponse<Task>>(
      `/api/v1/tasks`,
      data
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseTaskDates<Task>(response.data.data);
    }
    return response.data;
  }

  /**
   * Update an existing task (Student, if not locked)
   */
  async updateTask(
    taskId: number,
    data: UpdateTaskRequest
  ): Promise<ApiResponse<Task>> {
    const response = await apiClient.put<ApiResponse<Task>>(
      `/api/v1/tasks/${taskId}`,
      {...data, projectId: 0} // TODO: Temporary fix for backend requirement
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseTaskDates<Task>(response.data.data);
    }
    return response.data;
  }

  /**
   * Delete a task by ID (Student, if not locked)
   */
  async deleteTask(taskId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/v1/tasks/${taskId}`
    );
    return response.data;
  }

  /**
   * Toggle task completion status (Student)
   */
  async toggleTaskStatus(
    taskId: number,
    status: "IN_PROGRESS" | "COMPLETED"
  ): Promise<ApiResponse<null>> {
    const response = await apiClient.patch<ApiResponse<null>>(
      `/api/v1/tasks/${taskId}/status`,
      null,
      { params: { status } }
    );
    return response.data;
  }

  /**
   * Toggle task lock status (Instructor only)
   */
  async toggleTaskLock(
    taskId: number,
    data: ToggleTaskLockRequest
  ): Promise<ApiResponse<Task>> {
    const response = await apiClient.patch<ApiResponse<Task>>(
      `/api/v1/tasks/${taskId}/lock`,
      data
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseTaskDates<Task>(response.data.data);
    }
    return response.data;
  }
}

export const taskService = new TaskService();
