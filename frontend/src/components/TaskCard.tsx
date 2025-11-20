// TODO: Implement navigation to task detail page

import { Checkbox } from "@/components/ui/checkbox";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Calendar, Users, Lock } from "lucide-react";
import { useNavigate } from "react-router-dom";

interface TaskCardProps {
  id: number;
  projectId: number;
  milestoneId: number;
  title: string;
  assignees: Array<{ id: number; name: string; initials: string }>;
  startDate: Date;
  endDate: Date;
  completed: boolean;
  isLocked: boolean;
  onToggle: () => void;
}

export const TaskCard = ({
  id,
  projectId,
  milestoneId,
  title,
  assignees,
  startDate,
  endDate,
  completed,
  isLocked = false,
  onToggle,
}: TaskCardProps) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/project/${projectId}/milestone/${milestoneId}/task/${id}`);
  };

  return (
    <div className="flex items-start gap-3 p-4 rounded-lg border border-border bg-card hover:bg-secondary/50 transition-colors">
      <Checkbox 
        checked={completed} 
        onCheckedChange={onToggle}
        className="mt-1"
        disabled={isLocked}
      />
      <div className="flex-1 space-y-2 cursor-pointer" onClick={handleClick}>
        <div className="flex items-start justify-between gap-2">
          <div className="flex items-center gap-2">
            <h4 className={`font-medium text-foreground ${completed ? "line-through text-muted-foreground" : ""}`}>
              {title}
            </h4>
            {isLocked && (
              <Lock className="w-3 h-3 text-destructive" />
            )}
          </div>
        </div>
        <div className="flex items-center gap-4 text-sm text-muted-foreground">
          <div className="flex items-center gap-1">
            <Users className="w-3 h-3" />
            <div className="flex -space-x-2">
              {assignees.slice(0, 3).map((assignee) => (
                <Avatar key={assignee.id} className="w-5 h-5 border-2 border-card">
                  <AvatarFallback className="bg-primary text-primary-foreground text-[10px]">
                    {assignee.initials}
                  </AvatarFallback>
                </Avatar>
              ))}
              {assignees.length > 3 && (
                <div className="w-5 h-5 rounded-full bg-muted border-2 border-card flex items-center justify-center">
                  <span className="text-[10px] text-muted-foreground">+{assignees.length - 3}</span>
                </div>
              )}
            </div>
          </div>
          <div className="flex items-center gap-1">
            <Calendar className="w-3 h-3" />
            <span>{endDate.toDateString()}</span>
          </div>
        </div>
      </div>
    </div>
  );
};
