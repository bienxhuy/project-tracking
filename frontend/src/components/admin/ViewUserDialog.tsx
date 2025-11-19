import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { User, UserRole, UserStatus } from "@/types/user.type";
import { Calendar, Mail, Shield, Hash, Activity } from "lucide-react";

interface ViewUserDialogProps {
  open: boolean;
  user: User | null;
  onOpenChange: (open: boolean) => void;
}

export function ViewUserDialog({
  open,
  user,
  onOpenChange
}: ViewUserDialogProps) {
  if (!user) return null;

  const getInitials = (firstName: string, lastName: string) => {
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
  };

  const getRoleBadgeColor = (role: UserRole) => {
    switch (role) {
      case UserRole.STUDENT:
        return "bg-blue-100 text-blue-800";
      case UserRole.INSTRUCTOR:
        return "bg-purple-100 text-purple-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const getStatusBadgeColor = (status: UserStatus) => {
    return status === UserStatus.ACTIVE
      ? "bg-green-100 text-green-800"
      : "bg-gray-100 text-gray-800";
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    });
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>User Details</DialogTitle>
          <DialogDescription>
            Complete information about this user account.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6 py-4">
          {/* Avatar and Basic Info */}
          <div className="flex items-start gap-4">
            <Avatar className="h-20 w-20">
              <AvatarImage src={user.avatar} />
              <AvatarFallback className="text-2xl">
                {getInitials(user.firstName, user.lastName)}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <h3 className="text-2xl font-semibold">
                {user.firstName} {user.lastName}
              </h3>
              <div className="flex gap-2 mt-2">
                <Badge className={getRoleBadgeColor(user.role)}>
                  {user.role}
                </Badge>
                <Badge className={getStatusBadgeColor(user.status)}>
                  {user.status}
                </Badge>
              </div>
            </div>
          </div>

          {/* Contact Information */}
          <div className="space-y-3">
            <h4 className="font-semibold text-sm text-muted-foreground uppercase">
              Contact Information
            </h4>
            <div className="grid gap-2">
              <div className="flex items-center gap-3 text-sm">
                <Mail className="h-4 w-4 text-muted-foreground" />
                <span>{user.email}</span>
              </div>
              <div className="flex items-center gap-3 text-sm">
                <Hash className="h-4 w-4 text-muted-foreground" />
                <span>{user.studentId || user.instructorId || "N/A"}</span>
              </div>
            </div>
          </div>

          {/* Account Information */}
          <div className="space-y-3">
            <h4 className="font-semibold text-sm text-muted-foreground uppercase">
              Account Information
            </h4>
            <div className="grid gap-2">
              <div className="flex items-center gap-3 text-sm">
                <Shield className="h-4 w-4 text-muted-foreground" />
                <span>Role: {user.role}</span>
              </div>
              <div className="flex items-center gap-3 text-sm">
                <Calendar className="h-4 w-4 text-muted-foreground" />
                <span>Created: {formatDate(user.createdAt)}</span>
              </div>
              <div className="flex items-center gap-3 text-sm">
                <Calendar className="h-4 w-4 text-muted-foreground" />
                <span>Last Updated: {formatDate(user.updatedAt)}</span>
              </div>
              {user.lastLogin && (
                <div className="flex items-center gap-3 text-sm">
                  <Activity className="h-4 w-4 text-muted-foreground" />
                  <span>Last Login: {formatDate(user.lastLogin)}</span>
                </div>
              )}
            </div>
          </div>

          {/* Activity Summary */}
          {(user.projectCount || user.totalTasks) && (
            <div className="space-y-3">
              <h4 className="font-semibold text-sm text-muted-foreground uppercase">
                Activity Summary
              </h4>
              <div className="grid grid-cols-3 gap-4">
                <div className="border rounded-lg p-4 text-center">
                  <div className="text-2xl font-bold text-blue-600">
                    {user.projectCount || 0}
                  </div>
                  <div className="text-xs text-muted-foreground mt-1">Projects</div>
                </div>
                <div className="border rounded-lg p-4 text-center">
                  <div className="text-2xl font-bold text-green-600">
                    {user.completedTasks || 0}
                  </div>
                  <div className="text-xs text-muted-foreground mt-1">Completed</div>
                </div>
                <div className="border rounded-lg p-4 text-center">
                  <div className="text-2xl font-bold text-gray-600">
                    {user.totalTasks || 0}
                  </div>
                  <div className="text-xs text-muted-foreground mt-1">Total Tasks</div>
                </div>
              </div>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

