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

class MilestoneService {
  /**
   * Get all milestones for a project
   */
  async getMilestonesByProject(projectId: number): Promise<ApiResponse<Milestone[]>> {
    const response = await apiClient.get<ApiResponse<Milestone[]>>(
      `/api/v1/projects/${projectId}/milestones`
    );
    return response.data;
  }

  /**
   * Get milestone detail by ID
   */
  async getMilestoneById(milestoneId: number): Promise<ApiResponse<MilestoneDetail>> {
    const response = await apiClient.get<ApiResponse<MilestoneDetail>>(
      `/api/v1/milestones/${milestoneId}`
    );
    return response.data;
  }

  /**
   * Create a new milestone in a project (Student)
   */
  async createMilestone(
    projectId: number,
    data: CreateMilestoneRequest
  ): Promise<ApiResponse<Milestone>> {
    const response = await apiClient.post<ApiResponse<Milestone>>(
      `/api/v1/projects/${projectId}/milestones`,
      data
    );
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
      data
    );
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
    return response.data;
  }
}

export const milestoneService = new MilestoneService();
