import { z } from 'zod';

export const TaskSchema = z
  .object({
    title: z
      .string()
      .describe('Brief name or summary of the task'),
    description: z
      .string()
      .describe(
        'Detailed explanation of what needs to be accomplished in this task'
      ),
    startDate: z
      .coerce.date()
      .describe(
        'When this task should begin (must be within milestone date range)'
      ),
    endDate: z
      .coerce.date()
      .describe(
        'When this task should be completed (must be within milestone date range)'
      ),
  })
  .refine((data) => data.endDate >= data.startDate, {
    message: 'Task end date must be after or equal to start date',
    path: ['endDate'],
  });
