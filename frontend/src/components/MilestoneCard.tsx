// TODO: Enable navigation when the milestone detail page is ready
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { CheckCircle2, Clock, Lock } from "lucide-react";
// import { useNavigate } from "react-router-dom";

interface MilestoneCardProps {
  id: number;
  projectId: number;
  title: string;
  description: string;
  progress: number;
  tasksTotal: number;
  tasksCompleted: number;
  status: "LOCKED" | "IN_PROGRESS" | "COMPLETED";
}

export const MilestoneCard = ({
  // id,
  // projectId,
  title,
  description,
  progress,
  tasksTotal,
  tasksCompleted,
  status,
}: MilestoneCardProps) => {
  // const navigate = useNavigate();

  // const handleClick = () => {
  //   navigate(`/project/${projectId}/milestone/${id}`);
  // };

  return (
    <Card
      className="border-border bg-card cursor-pointer hover:bg-secondary/50 transition-colors"
    // onClick={handleClick}
    >
      <CardHeader>
        <div className="flex items-start justify-between">
          <CardTitle className="text-lg font-semibold text-foreground flex items-center gap-2">
            {status === "COMPLETED" ? (
              <CheckCircle2 className="w-5 h-5 text-success" />
            ) : status === "IN_PROGRESS" ? (
              <Clock className="w-5 h-5 text-primary" />
            ) : (
              <Lock className="w-5 h-5 text-red-400" />
            )}
            {title}
          </CardTitle>
        </div>
        <p className="text-sm text-muted-foreground mt-2">{description}</p>
      </CardHeader>
      <CardContent className="space-y-3">
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
