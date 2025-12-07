import { z } from 'zod';
import { MilestoneSchema } from './milestone.schema.js';

export const ProjectTreeSchema = z.object({
  content: z
    .string()
    .describe(
      'Detailed description of the project scope, purpose, and overview'
    ),
  objectives: z
    .string()
    .describe('Specific goals and outcomes the project aims to achieve'),
  milestones: z
    .array(MilestoneSchema)
    .describe(
      'Key milestones and deliverables that make up the project timeline'
    ),
});
