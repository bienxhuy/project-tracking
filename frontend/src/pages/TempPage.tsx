import { useEffect, useState } from "react";
import { fetchTempProjects } from "@/services/project.service";
import { fetchTempMilestones } from "@/services/milestone.service";
import { fetchTempTasks } from "@/services/task.service";

import { Project } from "@/types/project.type";
import { Milestone } from "@/types/milestone.type";
import { Task } from "@/types/task.type";
import { ProjectCard } from "@/components/ProjectCard";
import { MilestoneCard } from "@/components/MilestoneCard";
import { TaskCard } from "@/components/TaskCard";


export const TempPage = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [milestones, setMilestones] = useState<Milestone[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);

  useEffect(() => {
    // TODO: Replace with real data fetching logic
    const projects = fetchTempProjects();
    setProjects(projects);

    const milestones = fetchTempMilestones();
    setMilestones(milestones);

    const tasks = fetchTempTasks();
    setTasks(tasks);
  }, []);

  return (
    <div className="p-8 bg-background min-h-screen flex flex-col items-center">
      {projects.map((project) => (
        <>
          <ProjectCard key={project.id}
            id={project.id}
            title={project.title}
            semester={project.semester}
            year={project.year}
            batch={project.batch}
            progress={project.completionPercentage}
            members={project.memberCount}
            milestones={project.milestoneCount}
            completedMilestones={1}
            status={project.status}
          />
          <div className="h-4" />
        </>
      ))}

      <div className="h-8" />

      {milestones.map((milestone) => (
        <>
          <MilestoneCard key={milestone.id}
            id={milestone.id}
            projectId="m1"
            title={milestone.title}
            description={milestone.description}
            progress={milestone.completionPercentage}
            tasksTotal={milestone.tasksTotal}
            tasksCompleted={milestone.tasksCompleted}
            completed={milestone.status === "COMPLETED"}
          />
          <div className="h-4" />
        </>
      ))}

      <div className="h-8" />

      {tasks.map((task) => (
        <>
          <TaskCard key={task.id}
            id={task.id}
            projectId="p1"
            milestoneId="m1"
            title={task.title}
            assignees={task.assignees}
            endDate={task.endDate}
            completed={task.completed}
            isLocked={task.isLocked}
            onToggle={() => { }} />
          <div className="h-4" />
        </>
      ))}
    </div>
  );
};