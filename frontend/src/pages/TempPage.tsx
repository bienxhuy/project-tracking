import { ProjectCard } from "@/components/ProjectCard";
import { useEffect, useState } from "react";
import { fetchTempProjects } from "@/services/project.service";
import { ProjectCard as ProjectCardProps } from "@/types/projectCard.type";

export const TempPage = () => {
  const [projects, setProjects] = useState<ProjectCardProps[]>([]);

  useEffect(() => {
    // TODO: Replace with real data fetching logic
    const projects = fetchTempProjects();
    setProjects(projects);
  }, []);

  return (
    <div className="p-8 bg-background min-h-screen flex flex-col items-center">
      {projects.map((project) => (
        <>
          <ProjectCard key={project.id} {...project} />
          <div className="h-4" />
        </>
      ))}
    </div>
  );
};