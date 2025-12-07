import { z } from 'zod';
import { TaskSchema } from './task.schema.js';

export const MilestoneSchema = z
  .object({
    id: z.number().describe('Temporary unique identifier for the milestone'),
    title: z.string().describe('Name of the milestone or key deliverable'),
    description: z
      .string()
      .describe(
        'Detailed description of what this milestone represents and what needs to be achieved'
      ),
    startDate: z
      .coerce.date()
      .describe(
        'When work on this milestone should begin (must be within project date range)'
      ),
    endDate: z
      .coerce.date()
      .describe(
        'When this milestone should be completed (must be within project date range)'
      ),
    tasks: z
      .array(TaskSchema)
      .describe(
        'List of tasks that need to be completed to achieve this milestone'
      ),
  })
  .refine((data) => data.endDate >= data.startDate, {
    message: 'Milestone end date must be after or equal to start date',
    path: ['endDate'],
  })
  .refine(
    (data) => {
      return data.tasks.every(
        (task) => task.startDate >= data.startDate && task.endDate <= data.endDate
      );
    },
    {
      message: 'All tasks must have dates within the milestone date range',
      path: ['tasks'],
    }
  );
