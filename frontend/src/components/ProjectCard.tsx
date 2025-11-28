import { useNavigate } from "react-router-dom";
import { statusConfig } from "@/types/project.type";

import { Calendar, Users, Target, Edit, Trash2, MoreVertical, Lock } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";


export interface ProjectCardProps {
  id: number;
  title: string;
  semester: number;
  year: number;
  batch: string;
  progress: number;
  members: number;
  milestones: number;
  completedMilestones: number;
  status: "ACTIVE" | "COMPLETED";
  isLocked: boolean;
  showActions?: boolean;
  onUpdate?: () => void;
  onDelete?: () => void;
}


export const ProjectCard = ({
  id,
  title,
  semester,
  year,
  batch,
  progress,
  members,
  milestones,
  completedMilestones,
  status,
  isLocked,
  showActions = false,
  onUpdate,
  onDelete,
}: ProjectCardProps) => {
  const navigate = useNavigate();
  const statusInfo = statusConfig[status];

  const handleCardClick = () => {
    navigate(`/project/${id}`);
  };

  const handleActionClick = (e: React.MouseEvent, action: () => void) => {
    e.stopPropagation();
    action();
  };

  return (
    <Card 
      className="hover:shadow-lg transition-all duration-300 border-border bg-gradient-card cursor-pointer bg-dark"
      onClick={handleCardClick}
    >
      <CardHeader>
        <div className="flex items-start justify-between">
          <CardTitle className="text-xl font-semibold text-foreground">{title}</CardTitle>
          <div className="flex items-center gap-2">
            <Badge className={statusInfo.className}>{statusInfo.label}</Badge>
            {isLocked && (
              <Badge variant="outline" className="flex items-center gap-1 border-yellow-500 text-yellow-600">
                <Lock className="h-3 w-3" />
                Locked
              </Badge>
            )}
            {showActions && (
              <DropdownMenu>
                <DropdownMenuTrigger asChild onClick={(e) => e.stopPropagation()}>
                  <Button variant="ghost" size="icon" className="h-8 w-8">
                    <MoreVertical className="h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem 
                    onClick={(e) => handleActionClick(e, onUpdate!)}
                    disabled={isLocked}
                  >
                    <Edit className="mr-2 h-4 w-4" />
                    Cập nhật
                  </DropdownMenuItem>
                  <DropdownMenuItem 
                    onClick={(e) => handleActionClick(e, onDelete!)}
                    className="text-destructive"
                    disabled={isLocked}
                  >
                    <Trash2 className="mr-2 h-4 w-4" />
                    Xóa
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>
        </div>
        <div className="flex items-center gap-4 text-sm text-muted-foreground mt-2">
          <div className="flex items-center gap-1">
            <Calendar className="w-4 h-4" />
            <span>Học kỳ {semester} {year} - Đợt {batch}</span>
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
