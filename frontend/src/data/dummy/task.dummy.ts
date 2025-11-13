import { TaskCard } from "@/types/taskCard.type";

export const tasks: TaskCard[] = [
  {
    id: "t1",
    projectId: "1",
    milestoneId: "m1",
    title: "Design Database Schema",
    assignees: [
      { id: "1", name: "John Doe", initials: "JD" },
      { id: "2", name: "Jane Smith", initials: "JS" }
    ],
    dueDate: "2024-03-15",
    completed: false,
    isLocked: false,
    onToggle: () => {}
  },
  {
    id: "t2",
    projectId: "1",
    milestoneId: "m1",
    title: "Create API Endpoints",
    assignees: [
      { id: "3", name: "Mike Johnson", initials: "MJ" }
    ],
    dueDate: "2024-03-20",
    completed: false,
    isLocked: true,
    onToggle: () => {}
  }
];
