export interface TaskCard {
  id: string;
  projectId: string;
  milestoneId: string;
  title: string;
  assignees: Array<{ id: string; name: string; initials: string }>;
  dueDate: string;
  completed: boolean;
  isLocked?: boolean;
  onToggle: () => void;
}
