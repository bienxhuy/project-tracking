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
import { BaseUser, ProjectMemberResponse } from "@/types/user.type";

/**
 * Helper function to parse date strings to Date objects in Project
 */
function parseProjectDates<T extends Project | ProjectDetail>(project: any): T {
  return {
    ...project,
    startDate: new Date(project.startDate),
    endDate: new Date(project.endDate),
    students: project.students.filter((mem: any) => mem.role === "STUDENT"),
    milestones: project.milestones?.map((milestone: any) => ({
      ...milestone,
      startDate: new Date(milestone.startDate),
      endDate: new Date(milestone.endDate),
    })),
  };
}

class ProjectService {
  /**
   * Get project detail by ID
   */
  async getProjectById(projectId: number): Promise<ApiResponse<ProjectDetail>> {
    const response = await apiClient.get<ApiResponse<ProjectDetail>>(
      `/api/v1/projects/${projectId}`
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseProjectDates<ProjectDetail>(response.data.data);
    }
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
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseProjectDates<Project>(response.data.data);
    }
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
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseProjectDates<Project>(response.data.data);
    }
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
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseProjectDates<Project>(response.data.data);
    }
    return response.data;
  }

  /**
   * Lock project objective and description (Instructor only)
   */
  async lockProjectContent(projectId: number): Promise<ApiResponse<null>> {
    const response = await apiClient.patch<ApiResponse<null>>(
      `/api/v1/projects/${projectId}/content/lock`
    );
    return response.data;
  }

  /**
   * Unlock project objective and description (Instructor only)
   */
  async unlockProjectContent(projectId: number): Promise<ApiResponse<null>> {
    const response = await apiClient.patch<ApiResponse<null>>(
      `/api/v1/projects/${projectId}/content/unlock`
    );
    return response.data;
  }

  /**
   * Lock entire project including content, milestones, tasks, and reports (Instructor only)
   */
  async lockEntireProject(projectId: number): Promise<ApiResponse<null>> {
    const response = await apiClient.patch<ApiResponse<null>>(
      `/api/v1/projects/${projectId}/lock`
    );
    return response.data;
  }

  /**
   * Unlock entire project including content, milestones, tasks, and reports (Instructor only)
   */
  async unlockEntireProject(projectId: number): Promise<ApiResponse<null>> {
    const response = await apiClient.patch<ApiResponse<null>>(
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
    if (response.data.status === "success" && response.data.data) {
      response.data.data = response.data.data.map(project => parseProjectDates<Project>(project));
    }
    return response.data;
  }

  /**
   * Get student's projects
   */
  async getStudentProjects(
    studentId: number,
    params?: {
      year?: number;
      semester?: number;
      batch?: string;
    }
  ): Promise<ApiResponse<Project[]>> {
    const response = await apiClient.get<ApiResponse<Project[]>>(
      `/api/v1/projects/student/${studentId}`,
      { params }
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = response.data.data.map(project => parseProjectDates<Project>(project));
    }
    return response.data;
  }

  /**
   * Get instructor's projects
   */
  async getInstructorProjects(
    instructorId: number
  ): Promise<ApiResponse<Project[]>> {
    const response = await apiClient.get<ApiResponse<Project[]>>(
      `/api/v1/projects/instructor/${instructorId}`
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = response.data.data.map(project => parseProjectDates<Project>(project));
    }
    return response.data;
  }

  /**
   * Export project as PDF (Instructor only)
   */
  async exportProjectAsPDF(projectId: number): Promise<void> {
    try {
      const response = await apiClient.get(
        `/api/v1/projects/${projectId}/export-pdf`,
        {
          responseType: 'blob',
        }
      );

      // Extract filename from Content-Disposition header
      const contentDisposition = response.headers['content-disposition'];
      let filename = `project_${projectId}.pdf`;
      
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/i);
        if (filenameMatch && filenameMatch[1]) {
          filename = filenameMatch[1];
        }
      }

      // Create blob and download
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error exporting project as PDF:', error);
      throw error;
    }
  }

  /**
   * Get project members and parse to BaseUser array
   */
  async getProjectMembers(projectId: number): Promise<BaseUser[]> {
    const response = await apiClient.get<ApiResponse<ProjectMemberResponse[]>>(
      `/api/v1/project-members/project/${projectId}`
    );
    
    // Parse ProjectMemberResponse array to BaseUser array
    return response.data.data.map((member) => ({
      id: member.userId,
      displayName: member.userName,
      email: member.userEmail,
      role: member.role,
    }));
  }
}

export const projectService = new ProjectService();
