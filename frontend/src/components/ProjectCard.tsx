// TODO: Uncomment the navigation code when routing is set up

// import { useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Calendar, Users, Target } from "lucide-react";

export interface ProjectCardProps {
  id: string;
  title: string;
  semester: string;
  year: string;
  batch: string;
  progress: number;
  members: number;
  milestones: number;
  completedMilestones: number;
  status: "active" | "completed" | "locked";
}

const statusConfig = {
  active: { label: "Active", className: "bg-primary text-primary-foreground" },
  completed: { label: "Completed", className: "bg-success text-success-foreground" },
  locked: { label: "Locked", className: "bg-warning text-warning-foreground" },
};

export const ProjectCard = ({
  // id,
  title,
  semester,
  year,
  batch,
  progress,
  members,
  milestones,
  completedMilestones,
  status,
}: ProjectCardProps) => {
  // const navigate = useNavigate();
  const statusInfo = statusConfig[status];

  return (
    <Card 
      className="hover:shadow-lg transition-all duration-300 border-border bg-gradient-card cursor-pointer bg-dark"
      // onClick={() => navigate(`/project/${id}`)}
    >
      <CardHeader>
        <div className="flex items-start justify-between">
          <CardTitle className="text-xl font-semibold text-foreground">{title}</CardTitle>
          <Badge className={statusInfo.className}>{statusInfo.label}</Badge>
        </div>
        <div className="flex items-center gap-4 text-sm text-muted-foreground mt-2">
          <div className="flex items-center gap-1">
            <Calendar className="w-4 h-4" />
            <span>{semester} {year} - Batch {batch}</span>
          </div>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div>
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm font-medium text-foreground">Overall Progress</span>
            <span className="text-sm font-bold text-primary">{progress}%</span>
          </div>
          <Progress value={progress} className="h-2" />
        </div>

        <div className="grid grid-cols-2 gap-4 pt-2">
          <div className="flex items-center gap-2">
            <Users className="w-4 h-4 text-muted-foreground" />
            <div>
              <p className="text-xs text-muted-foreground">Team Members</p>
              <p className="text-sm font-semibold text-foreground">{members}</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Target className="w-4 h-4 text-muted-foreground" />
            <div>
              <p className="text-xs text-muted-foreground">Milestones</p>
              <p className="text-sm font-semibold text-foreground">
                {completedMilestones}/{milestones}
              </p>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
