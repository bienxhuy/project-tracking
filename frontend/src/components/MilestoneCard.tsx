import { useNavigate } from "react-router-dom";
import { useState } from "react";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { CheckCircle2, Clock, SquarePen, Lock, Trash } from "lucide-react";
import { Button } from "./ui/button";

interface MilestoneCardProps {
  id: number;
  projectId: number;
  title: string;
  description: string;
  startDate: Date;
  endDate: Date;
  progress: number;
  tasksTotal: number;
  tasksCompleted: number;
  status: "LOCKED" | "IN_PROGRESS" | "COMPLETED";
}

export const MilestoneCard = ({
  id,
  projectId,
  title,
  description,
  startDate,
  endDate,
  progress,
  tasksTotal,
  tasksCompleted,
  status,
}: MilestoneCardProps) => {
  const navigate = useNavigate();

  // State to manage milestone
  const [isEditting, setIsEditting] = useState<boolean>(false);

  return (
    <Card
      className="border-border bg-card hover:bg-secondary/10 transition-colors"
    >
      <CardHeader>
        <CardTitle className="flex flex-col items-start text-lg font-semibold text-foreground">
          <div className="flex items-center justify-between w-full">
            {/* Icon based on status */}
            {status === "COMPLETED" ? (
              <CheckCircle2 className="w-5 h-5 text-success" />
            ) : status === "IN_PROGRESS" ? (
              <Clock className="w-5 h-5 text-primary" />
            ) : (
              <Lock className="w-5 h-5 text-red-400" />
            )}

            {/* Functional buttons to update & delete */}
            <div className="flex items-center gap-2">
              <Button
                size="icon"
                variant="ghost"
                className="rounded-full cursor-pointer"
                onClick={() => setIsEditting(true)}>
                <SquarePen />
              </Button>
              <Button
                size="icon"
                variant="destructive"
                className="rounded-full cursor-pointer"
                onClick={() => { console.log("Delete milestone") }}>
                <Trash />
              </Button>
            </div>
          </div>
          <div
            className="cursor-pointer hover:underline"
            onClick={() => navigate(`/projects/${projectId}/milestones/${id}`)}
          >
            {title}
          </div>
        </CardTitle>
        <p className="text-sm text-muted-foreground mt-2">{description}</p>
      </CardHeader>
      <CardContent className="space-y-3">
        <div>
          <span className="text-sm text-muted-foreground">
            {startDate?.toLocaleDateString()} - {endDate?.toLocaleDateString()}
          </span>
        </div>
        <div>
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm text-foreground">Tiến độ</span>
            <span className="text-sm font-bold text-primary">{progress}%</span>
          </div>
          <Progress value={progress} className="h-2" />
        </div>
        <div className="flex items-center justify-between text-sm">
          <span className="text-muted-foreground">
            {status === "LOCKED" ? "Đã khóa" : status === "IN_PROGRESS" ? "Đang tiến hành" : "Hoàn thành"}
          </span>
          <span className="font-semibold text-foreground">
            {tasksCompleted}/{tasksTotal}
          </span>
        </div>
      </CardContent>
    </Card>
  );
};
