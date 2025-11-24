// API Service to fetch project data

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import { Project } from "@/types/project.type";

class ProjectService {
  async getProjects(): Promise<Project[]> {
    const response = await apiClient.get<ApiResponse<Project[]>>("/api/v1/projects");
    return response.data.data;
  }
}

export const projectService = new ProjectService();

