// API Service to fetch project data

import { currentBatchProjects, projectDetailDummy } from "@/data/dummy/projects.dummy";
import { CreateProjectRequest, UpdateProjectRequest, Project } from "@/types/project.type";

// Dummy faculty data
const faculties = [
  "Công nghệ Thông tin",
  "Đào tạo quốc tế",
  "Cơ khí Chế tạo máy",
];

export function fetchTempProjects() {
  // TODO: Replace with real API call
  return currentBatchProjects;
}

export function fetchDetailProject(projectId: number) {
  // TODO: Replace with real API call
  // For now, return the dummy data regardless of projectId
  return projectDetailDummy;
}

export function fetchAllFaculties(): string[] {
  // TODO: Replace with real API call
  return faculties;
}

export function createProject(data: CreateProjectRequest): Promise<Project> {
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

export function updateProject(data: UpdateProjectRequest): Promise<Project> {
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

export function deleteProject(projectId: number): Promise<void> {
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
