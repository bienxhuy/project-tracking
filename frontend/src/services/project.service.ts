// API Service to fetch project data

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import {
  Project,
  ProjectDetail,
  CreateProjectRequest,
  UpdateProjectRequest,
  UpdateProjectContentRequest,
} from "@/types/project.type";

class ProjectService {
  /**
   * Get project detail by ID
   */
  async getProjectById(projectId: number): Promise<ApiResponse<ProjectDetail>> {
    const response = await apiClient.get<ApiResponse<ProjectDetail>>(
      `/api/v1/projects/${projectId}`
    );
    return response.data;
  }

  /**
   * Create a new project (Instructor only)
   */
  async createProject(data: CreateProjectRequest): Promise<ApiResponse<Project>> {
    const response = await apiClient.post<ApiResponse<Project>>(
      '/api/v1/projects',
      data
    );
    return response.data;
  }

  /**
   * Update an existing project (Instructor only)
   */
  async updateProject(projectId: number, data: UpdateProjectRequest): Promise<ApiResponse<Project>> {
    const response = await apiClient.put<ApiResponse<Project>>(
      `/api/v1/projects/${projectId}`,
      data
    );
    return response.data;
  }

  /**
   * Delete a project by ID (Instructor only)
   */
  async deleteProject(projectId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/v1/projects/${projectId}`
    );
    return response.data;
  }

  /**
   * Update project objective and content (Student)
   */
  async updateProjectContent(
    projectId: number,
    data: UpdateProjectContentRequest
  ): Promise<ApiResponse<Project>> {
    const response = await apiClient.patch<ApiResponse<Project>>(
      `/api/v1/projects/${projectId}/content`,
      data
    );
    return response.data;
  }

  /**
   * Lock project objective and description (Instructor only)
   */
  async lockProject(projectId: number): Promise<ApiResponse<Project>> {
    const response = await apiClient.patch<ApiResponse<Project>>(
      `/api/v1/projects/${projectId}/lock`
    );
    return response.data;
  }

  /**
   * Unlock project objective and description (Instructor only)
   */
  async unlockProject(projectId: number): Promise<ApiResponse<Project>> {
    const response = await apiClient.patch<ApiResponse<Project>>(
      `/api/v1/projects/${projectId}/unlock`
    );
    return response.data;
  }

  /**
   * Get all projects (Admin only)
   */
  async getProjects(): Promise<ApiResponse<Project[]>> {
    const response = await apiClient.get<ApiResponse<Project[]>>(
      '/api/v1/projects'
    );
    return response.data;
  }

  /**
   * Get student's projects
   */
  async getStudentProjects(params?: {
    year?: number;
    semester?: number;
    batch?: number;
  }): Promise<ApiResponse<Project[]>> {
    const response = await apiClient.get<ApiResponse<Project[]>>(
      '/api/v1/projects/my-projects',
      { params }
    );
    return response.data;
  }

  /**
   * Get instructor's projects
   */
  async getInstructorProjects(
    instructorId: number,
    params?: {
      year?: number;
      semester?: number;
      batch?: number;
    }
  ): Promise<ApiResponse<Project[]>> {
    const response = await apiClient.get<ApiResponse<Project[]>>(
      `/api/v1/projects/instructor/${instructorId}`,
      { params }
    );
    return response.data;
  }
}

export const projectService = new ProjectService();
