// API Service to fetch project data

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import { ProjectApiSummary, CreateProjectRequest, UpdateProjectRequest, Project, ProjectDetail } from "@/types/project.type";
import { currentBatchProjects, projectDetailDummy } from "@/data/dummy/projects.dummy";

// Dummy faculty data
const faculties = [
  "Công nghệ Thông tin",
  "Đào tạo quốc tế",
  "Cơ khí Chế tạo máy",
];

class ProjectService {
  /**
   * Get all projects
   */
  async getProjects(): Promise<ProjectApiSummary[]> {
    const response = await apiClient.get<ApiResponse<ProjectApiSummary[]>>("/api/v1/projects");
    return response.data.data;
  }

  /**
   * Get project detail by ID
   * TODO: Replace with real API call
   */
  fetchDetailProject(_projectId: number): ProjectDetail {
    // TODO: Replace with real API call
    // For now, return the dummy data regardless of projectId
    return projectDetailDummy;
  }

  /**
   * Get all faculties
   * TODO: Replace with real API call
   */
  fetchAllFaculties(): string[] {
    // TODO: Replace with real API call
    return faculties;
  }

  /**
   * Get temporary projects (dummy data)
   * TODO: Replace with real API call
   */
  fetchTempProjects(): Project[] {
    // TODO: Replace with real API call
    return currentBatchProjects;
  }

  /**
   * Create a new project
   * TODO: Replace with real API call
   */
  async createProject(data: CreateProjectRequest): Promise<Project> {
    // TODO: Replace with real API call
    return new Promise((resolve) => {
      setTimeout(() => {
        const newProject: Project = {
          id: Math.floor(Math.random() * 10000),
          title: data.title,
          objective: data.objective,
          content: data.content,
          year: data.year,
          semester: data.semester,
          batch: data.batch,
          falculty: data.falculty,
          startDate: new Date(),
          endDate: new Date(new Date().setMonth(new Date().getMonth() + 4)),
          memberCount: data.studentIds.length,
          milestoneCount: 0,
          completionPercentage: 0,
          status: "ACTIVE",
          isLocked: false,
        };
        currentBatchProjects.push(newProject);
        resolve(newProject);
      }, 500);
    });
  }

  /**
   * Update an existing project
   * TODO: Replace with real API call
   */
  async updateProject(data: UpdateProjectRequest): Promise<Project> {
    // TODO: Replace with real API call
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = currentBatchProjects.findIndex(p => p.id === data.id);
        if (index !== -1) {
          currentBatchProjects[index] = {
            ...currentBatchProjects[index],
            title: data.title,
            objective: data.objective,
            content: data.content,
            year: data.year,
            semester: data.semester,
            batch: data.batch,
            falculty: data.falculty,
            memberCount: data.studentIds.length,
          };
          resolve(currentBatchProjects[index]);
        } else {
          reject(new Error("Project not found"));
        }
      }, 500);
    });
  }

  /**
   * Delete a project by ID
   * TODO: Replace with real API call
   */
  async deleteProject(projectId: number): Promise<void> {
    // TODO: Replace with real API call
    return new Promise((resolve) => {
      setTimeout(() => {
        const index = currentBatchProjects.findIndex(p => p.id === projectId);
        if (index !== -1) {
          currentBatchProjects.splice(index, 1);
        }
        resolve();
      }, 500);
    });
  }
}

export const projectService = new ProjectService();
