// Simulate data from an API or database

import { Task } from "@/types/task.type";

export const tasks: Task[] = [
  {
    id: "t1",
    title: "Design Database Schema",
    description: "Create and optimize the database schema for the project.",
    assignees: [
      { id: "1", name: "John Doe", initials: "JD" },
      { id: "2", name: "Jane Smith", initials: "JS" }
    ],
    startDate: "2024-03-01",
    endDate: "2024-03-15",
    completed: false,
    isLocked: false,
  },
  {
    id: "t2",
    title: "Create API Endpoints",
    description: "Develop RESTful API endpoints for the application.",
    assignees: [
      { id: "3", name: "Mike Johnson", initials: "MJ" }
    ],
    startDate: "2024-03-05",
    endDate: "2024-03-20",
    completed: false,
    isLocked: true,
  }
];
