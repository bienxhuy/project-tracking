import { GoogleGenAI } from "@google/genai";
import { zodToJsonSchema } from "zod-to-json-schema";
import dotenv from "dotenv";
import { ProjectTreeSchema } from "../schemas/project.schema.js";

dotenv.config();

let ai = null;

/**
 * Initialize the Google GenAI client
 */
function initializeAI() {
  if (!ai) {
    ai = new GoogleGenAI({});
  }
}

/**
 * Generate a project breakdown with milestones and tasks using Gemini AI
 * @param {string} projectTitle - Title of the project (provided by instructor)
 * @param {string} projectDescription - Description of the project to generate
 * @param {Date|string} startDate - Project start date (provided by instructor)
 * @param {Date|string} endDate - Project end date (provided by instructor)
 * @returns {Promise<Object>} Generated project with milestones and tasks
 */
export async function generateProject(projectTitle, projectDescription, startDate, endDate) {
  // Initialize AI client if not already initialized
  if (!ai) {
    initializeAI();
  }

  // Prompt to generate project breakdown
  const prompt = `
You are a project management expert. Based on the following project information, create a detailed project breakdown with milestones and tasks.

IMPORTANT: Respond in the SAME LANGUAGE as the project description. If the description is in Vietnamese, respond in Vietnamese. If it's in English, respond in English. Match the user's language exactly.

Project Title: ${projectTitle}
Project Start Date: ${startDate}
Project End Date: ${endDate}

Project Description:
${projectDescription}

Generate a complete project plan that includes:
- Detailed content describing the project scope (in the same language as the description)
- Specific objectives the project aims to achieve (in the same language as the description)
- Multiple milestones that represent key phases or deliverables (in the same language as the description)
- For each milestone, include multiple tasks that need to be completed (in the same language as the description)
- Ensure all milestone and task dates fall within the project date range (${startDate} to ${endDate})
- Make sure end dates are after start dates at all levels

Be specific and practical in your breakdown. Each task should be actionable and clearly defined.
`;

  // Generate content using Gemini AI
  const response = await ai.models.generateContent({
    model: "gemini-2.5-flash",
    contents: prompt,
    config: {
      responseMimeType: "application/json",
      responseJsonSchema: zodToJsonSchema(ProjectTreeSchema),
    },
  });

  const generatedProject = ProjectTreeSchema.parse(JSON.parse(response.text));
  return generatedProject;
}
