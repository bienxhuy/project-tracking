// API Service to fetch project data

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import { ProjectApiSummary } from "@/types/project.type";

class ProjectService {
  async getProjects(): Promise<ProjectApiSummary[]> {
    const response = await apiClient.get<ApiResponse<ProjectApiSummary[]>>("/api/v1/projects");
    return response.data.data;
  }
}

export const projectService = new ProjectService();

