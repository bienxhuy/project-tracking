import { useEffect, useState } from "react";
import { fetchTempProjects } from "@/services/project.service";
import { fetchTempMilestones } from "@/services/milestone.service";

import { ProjectCard } from "@/components/ProjectCard";
import { ProjectCard as ProjectCardProps } from "@/types/projectCard.type";
import { MilestoneCard } from "@/components/MilestoneCard";
import { MilestoneCard as MilestoneCardProps } from "@/types/milestoneCard.type";


export const TempPage = () => {
  const [projects, setProjects] = useState<ProjectCardProps[]>([]);
  const [milestones, setMilestones] = useState<MilestoneCardProps[]>([]);

  useEffect(() => {
    // TODO: Replace with real data fetching logic
    const projects = fetchTempProjects();
    setProjects(projects);

    const milestones = fetchTempMilestones();
    setMilestones(milestones);
  }, []);

  return (
    <div className="p-8 bg-background min-h-screen flex flex-col items-center">
      {projects.map((project) => (
        <>
          <ProjectCard key={project.id} {...project} />
          <div className="h-4" />
        </>
      ))}

      <div className="h-8" />

      {milestones.map((milestone) => (
        <>
          <MilestoneCard key={milestone.id} {...milestone} />
          <div className="h-4" />  
        </>
      ))}

    </div>
  );
};