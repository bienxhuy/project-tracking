import { GenProjectTree } from "@/types/project.type";

const AI_SERVICE_URL = import.meta.env.VITE_AI_API_URL;

interface GenerateProjectRequest {
  projectTitle: string;
  projectDescription: string;
  startDate: string;
  endDate: string;
}

class AIService {
  /**
   * Generate project plan using AI
   */
  async generateProject(data: GenerateProjectRequest): Promise<GenProjectTree> {
    const response = await fetch(`${AI_SERVICE_URL}/api/generate-project`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      throw new Error("Failed to generate project");
    }

    const result = await response.json();

    // Transform dates from strings to Date objects and add expanded state
    const transformedData: GenProjectTree = {
      ...result,
      milestones: result.milestones.map((milestone: any) => ({
        ...milestone,
        startDate: new Date(milestone.startDate),
        endDate: new Date(milestone.endDate),
        isExpanded: false,
        tasks: milestone.tasks.map((task: any) => ({
          ...task,
          startDate: new Date(task.startDate),
          endDate: new Date(task.endDate),
          assignees: [],
        })),
      })),
    };

    return transformedData;
  }
}

export const aiService = new AIService();
