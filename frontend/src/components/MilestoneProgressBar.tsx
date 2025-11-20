import { useState } from "react";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";

interface Task {
  id: number;
  title: string;
  status: "COMPLETED" | "IN_PROGRESS";
}

interface TaskProgressBarProps {
  tasks: Task[];
  className?: string;
}

export const MilestoneProgressBar = ({ tasks, className = "" }: TaskProgressBarProps) => {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);
  
  // Calculate the width percentage for each task based on equal distribution
  const segmentWidth = 100 / tasks.length;
  
  // Calculate progress percentage for each task
  const getTaskProgress = (status: Task["status"]) => {
    switch (status) {
      case "COMPLETED": return 100;
      case "IN_PROGRESS": return 50;
      default: return 0;
    }
  };

  return (
    <TooltipProvider>
      <div className={`relative w-full h-3 bg-muted rounded-full overflow-hidden ${className}`}>
        <div className="absolute inset-0 flex">
          {tasks.map((task, index) => {
            const progress = getTaskProgress(task.status);
            return (
              <Tooltip key={task.id} delayDuration={0}>
                <TooltipTrigger asChild>
                  <div
                    className="relative h-full transition-all duration-200 cursor-pointer"
                    style={{ width: `${segmentWidth}%` }}
                    onMouseEnter={() => setHoveredIndex(index)}
                    onMouseLeave={() => setHoveredIndex(null)}
                  >
                    {/* Background segment */}
                    <div className="absolute inset-0 bg-muted" />
                    
                    {/* Progress fill */}
                    <div
                      className={`absolute inset-0 transition-all duration-300 ${
                        hoveredIndex === index
                          ? 'bg-primary brightness-110'
                          : 'bg-primary'
                      }`}
                      style={{
                        width: `${progress}%`,
                      }}
                    />
                    
                    {/* Divider line between segments (except for the last one) */}
                    {index < tasks.length - 1 && (
                      <div className="absolute right-0 top-0 w-[2px] h-full bg-background z-10" />
                    )}
                  </div>
                </TooltipTrigger>
                <TooltipContent side="top" className="bg-popover text-popover-foreground border border-border">
                  <div className="text-sm">
                    <p className="font-semibold">{task.title}</p>
                    <p className="text-muted-foreground">{progress}% complete</p>
                  </div>
                </TooltipContent>
              </Tooltip>
            );
          })}
        </div>
      </div>
    </TooltipProvider>
  );
};
