export interface MilestoneCard {
  id: string;
  projectId: string;
  title: string;
  description: string;
  progress: number;
  tasksTotal: number;
  tasksCompleted: number;
  completed: boolean;
}
