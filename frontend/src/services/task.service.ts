// API service for tasks

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import {
  Task,
  TaskDetail,
  CreateTaskRequest,
  UpdateTaskRequest,
  ToggleTaskStatusRequest,
  ToggleTaskLockRequest,
} from "@/types/task.type";

class TaskService {
  /**
   * Get all tasks for a milestone
   */
  async getTasksByMilestone(milestoneId: number): Promise<ApiResponse<Task[]>> {
    const response = await apiClient.get<ApiResponse<Task[]>>(
      `/api/v1/milestones/${milestoneId}/tasks`
    );
    return response.data;
  }

  /**
   * Get task detail by ID
   */
  async getTaskById(taskId: number): Promise<ApiResponse<TaskDetail>> {
    const response = await apiClient.get<ApiResponse<TaskDetail>>(
      `/api/v1/tasks/${taskId}`
    );
    return response.data;
  }

  /**
   * Create a new task in a milestone (Student)
   */
  async createTask(
    milestoneId: number,
    data: CreateTaskRequest
  ): Promise<ApiResponse<Task>> {
    const response = await apiClient.post<ApiResponse<Task>>(
      `/api/v1/milestones/${milestoneId}/tasks`,
      data
    );
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
      data
    );
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
    data: ToggleTaskStatusRequest
  ): Promise<ApiResponse<Task>> {
    const response = await apiClient.patch<ApiResponse<Task>>(
      `/api/v1/tasks/${taskId}/status`,
      data
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
    return response.data;
  }
}

export const taskService = new TaskService();
