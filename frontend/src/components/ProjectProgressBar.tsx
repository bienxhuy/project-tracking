import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";

interface Milestone {
  id: number;
  title: string;
  completionPercentage: number;
  tasksTotal: number;
  tasksCompleted: number;
  color?: string;
}

interface ProjectProgressBarProps {
  milestones: Milestone[];
  projectTotalTasks: number;
  projectId: number;
  className?: string;
}

export const ProjectProgressBar = ({ milestones, projectTotalTasks, projectId, className = "" }: ProjectProgressBarProps) => {
  const navigate = useNavigate();

  // State to track which milestone segment is hovered
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  // Replace milestone completionPercentage 
  // TODO: Fix back to completionPercentage calculation when BE is fixed
  milestones = milestones.map(milestone => ({
    ...milestone,
    completionPercentage: milestone.tasksTotal === 0 ? 0 : Math.round((milestone.tasksCompleted / milestone.tasksTotal) * 100)
  }));

  return (
    <TooltipProvider>
      <div className={`relative w-full h-4 bg-muted rounded-full${className}`}>
        <div className="absolute inset-0 flex">
          {milestones.map((milestone, index) => (
            <Tooltip key={milestone.id} delayDuration={0}>
              <TooltipTrigger asChild>
                <div
                  className="relative h-full transition-all duration-200 cursor-pointer overflow-hidden z-0 hover:scale-110 hover:z-100 hover:border hover:border-white hover:shadow-lg hover:rounded-md"
                  style={{ width: `${(milestone.tasksTotal / projectTotalTasks) * 100}%` }}
                  onMouseEnter={() => setHoveredIndex(index)}
                  onMouseLeave={() => setHoveredIndex(null)}
                  onClick={() => navigate(`/project/${projectId}/milestone/${milestone.id}`)}
                >
                  {/* Background segment */}
                  <div className="absolute inset-0 bg-gray-300" />

                  {/* Progress fill */}
                  <div
                    className={`absolute inset-0 transition-all duration-300 ${milestone.completionPercentage < 30
                      ? 'bg-red-500'
                      : milestone.completionPercentage < 70
                        ? 'bg-yellow-500'
                        : 'bg-green-500'
                      }`}
                    style={{
                      width: `${milestone.completionPercentage}%`,
                    }}
                  />

                  {/* Divider line between segments (except for the last one) */}
                  {index < milestones.length - 1 && hoveredIndex !== index && (
                    <div className="absolute right-0 top-0 w-[2px] h-full bg-background z-0" />
                  )}
                </div>
              </TooltipTrigger>
              <TooltipContent side="top" className="bg-popover text-popover-foreground border border-border">
                <div className="text-sm">
                  <p className="font-semibold">{milestone.title}</p>
                  <p className="text-muted-foreground">Tiến độ: {milestone.completionPercentage}%</p>
                </div>
              </TooltipContent>
            </Tooltip>
          ))}
        </div>
      </div>
    </TooltipProvider>
  );
};
