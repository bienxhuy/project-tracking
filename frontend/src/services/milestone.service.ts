// API Service to fetch milestone data

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import {
  Milestone,
  MilestoneDetail,
  CreateMilestoneRequest,
  UpdateMilestoneRequest,
  ToggleMilestoneLockRequest,
} from "@/types/milestone.type";

/**
 * Helper function to parse date strings to Date objects in Milestone
 */
function parseMilestoneDates<T extends Milestone | MilestoneDetail>(milestone: any): T {
  return {
    ...milestone,
    startDate: new Date(milestone.startDate),
    endDate: new Date(milestone.endDate),
    tasks: milestone.tasks?.map((task: any) => ({
      ...task,
      startDate: task.startDate ? new Date(task.startDate) : undefined,
      endDate: task.endDate ? new Date(task.endDate) : undefined,
    })),
  };
}

class MilestoneService {
  /**
   * Get all milestones for a project
   * @param projectId - The ID of the project
   * @param includeTasks - Whether to include tasks array in each milestone
   */
  async getMilestonesByProject(
    projectId: number,
    includeTasks?: boolean
  ): Promise<ApiResponse<Milestone[] | MilestoneDetail[]>> {
    const params = includeTasks ? { include: 'tasks' } : undefined;
    const response = await apiClient.get<ApiResponse<Milestone[] | MilestoneDetail[]>>(
      `/api/v1/milestones/project/${projectId}`,
      { params }
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = response.data.data.map((milestone: any) => 
        includeTasks 
          ? parseMilestoneDates<MilestoneDetail>(milestone)
          : parseMilestoneDates<Milestone>(milestone)
      );
    }
    return response.data;
  }

  /**
   * Get milestone detail by ID
   * @param milestoneId - The ID of the milestone
   * @param includeTasks - Whether to include tasks array in the milestone
   */
  async getMilestoneById(
    milestoneId: number,
    includeTasks?: boolean
  ): Promise<ApiResponse<Milestone | MilestoneDetail>> {
    const params = includeTasks ? { include: 'tasks' } : undefined;
    const response = await apiClient.get<ApiResponse<Milestone | MilestoneDetail>>(
      `/api/v1/milestones/${milestoneId}`,
      { params }
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = includeTasks
        ? parseMilestoneDates<MilestoneDetail>(response.data.data)
        : parseMilestoneDates<Milestone>(response.data.data);
    }
    return response.data;
  }

  /**
   * Create a new milestone in a project (Student)
   */
  async createMilestone(
    data: CreateMilestoneRequest
  ): Promise<ApiResponse<Milestone>> {
    const response = await apiClient.post<ApiResponse<Milestone>>(
      `/api/v1/milestones`,
      data
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseMilestoneDates<Milestone>(response.data.data);
    }
    return response.data;
  }

  /**
   * Update an existing milestone (Student, if not locked)
   */
  async updateMilestone(
    milestoneId: number,
    data: UpdateMilestoneRequest
  ): Promise<ApiResponse<Milestone>> {
    const response = await apiClient.put<ApiResponse<Milestone>>(
      `/api/v1/milestones/${milestoneId}`,
      {...data, projectId: 0} // TODO: Temporary fix for backend requirement
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseMilestoneDates<Milestone>(response.data.data);
    }
    return response.data;
  }

  /**
   * Delete a milestone by ID (Student, if not locked)
   */
  async deleteMilestone(milestoneId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/v1/milestones/${milestoneId}`
    );
    return response.data;
  }

  /**
   * Toggle milestone lock status (Instructor only)
   */
  async toggleMilestoneLock(
    milestoneId: number,
    data: ToggleMilestoneLockRequest
  ): Promise<ApiResponse<Milestone>> {
    const response = await apiClient.patch<ApiResponse<Milestone>>(
      `/api/v1/milestones/${milestoneId}/lock`,
      data
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseMilestoneDates<Milestone>(response.data.data);
    }
    return response.data;
  }
}

export const milestoneService = new MilestoneService();
