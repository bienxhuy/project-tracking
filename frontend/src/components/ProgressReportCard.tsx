import { useState, useRef } from "react";

import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import {
  ChevronDown,
  ChevronUp,
  Edit,
  FileText,
  Send,
  Lock,
  Unlock,
  Trash
} from "lucide-react";

import { Report } from "@/types/report.type";
import { Comment as CommentType } from "@/types/comment.type";
import { BaseUser } from "@/types/user.type";
import { ProgressReportEditor } from "@/components/ProgressReportEditor";
import { FileCard } from "@/components/FileCard";
import { reportService } from "@/services/report.service";
import { commentService } from "@/services/comment.service";
import { getInitials } from "@/utils/user.utils";
import { toast } from "sonner";

interface ProgressReportCardProps {
  report: Report;
  projectId: number;
  milestoneId: number;
  taskId: number;
  projectMembers: BaseUser[];
  userRole: "student" | "instructor";
  isTaskLocked: boolean;
  onReportUpdated?: (updatedReport: Report) => void; // Pass updated report to parent
}

export const ProgressReportCard = ({
  report,
  projectId,
  milestoneId,
  taskId,
  projectMembers,
  userRole,
  isTaskLocked,
  onReportUpdated,
}: ProgressReportCardProps) => {
  const { user } = useAuth();
  
  // General state of the card
  const [isExpanded, setIsExpanded] = useState(false);
  const [isEditing, setIsEditing] = useState(false);

  // Comments state and related
  // Lazy load comments when expanding
  const [comments, setComments] = useState<CommentType[]>([]);
  const [isLoadingComments, setIsLoadingComments] = useState(false);
  const [commentsLoaded, setCommentsLoaded] = useState(false);
  const [commentText, setCommentText] = useState("");

  // Mention dropdown state and related
  const [showMentionDropdown, setShowMentionDropdown] = useState(false);
  const [mentionSearch, setMentionSearch] = useState("");
  const [cursorPosition, setCursorPosition] = useState(0);
  const [mentionedUsers, setMentionedUsers] = useState<number[]>([]);

  // Ref for comment textarea 
  const commentInputRef = useRef<HTMLTextAreaElement>(null);

  // State to determine permissions
  const [isLocked, setIsLocked] = useState<boolean>(report.status === "LOCKED" || isTaskLocked);
  const isReporter = user?.id === report.reporter.id;
  const canEdit = isReporter && !isLocked;
  const canLock = userRole === "instructor";

  // Fetch report details (comments) when expanding for the first time
  const handleToggleExpand = async () => {
    // If current state is not expanded and comments not loaded, fetch comments
    // This is the 1st time expanding
    if (!isExpanded && !commentsLoaded) {
      setIsLoadingComments(true);
      try {
        const response = await commentService.getCommentsByReport(report.id);
        if (response.status === "success" && response.data) {
          setComments(response.data);
          setCommentsLoaded(true);
        } else {
          toast.error(response.message || "Không thể tải bình luận");
        }
      } catch (error) {
        toast.error("Đã xảy ra lỗi khi tải bình luận");
      } finally {
        setIsLoadingComments(false);
      }
    }

    // Toggle expanded state
    setIsExpanded(!isExpanded);
  };

  // Handle @ mention on every change in comment textarea
  const handleCommentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const value = e.target.value;
    const cursorPos = e.target.selectionStart;

    setCommentText(value);
    setCursorPosition(cursorPos);

    // Check for last @ character before cursor
    const textBeforeCursor = value.substring(0, cursorPos);
    const lastAtIndex = textBeforeCursor.lastIndexOf("@");

    // If found, extract the search term
    if (lastAtIndex !== -1) {
      // Extract search term after '@' 
      const searchTerm = textBeforeCursor.substring(lastAtIndex + 1);

      // If search term does not contain space, show mention dropdown
      if (!searchTerm.includes(" ")) {
        // Update search term and show dropdown
        setMentionSearch(searchTerm.toLowerCase());
        setShowMentionDropdown(true);
        return;
      }
    }

    setShowMentionDropdown(false);
  };

  // Insert mention
  const insertMention = (member: { id: number; displayName: string }) => {
    const textBeforeCursor = commentText.substring(0, cursorPosition);
    const textAfterCursor = commentText.substring(cursorPosition);
    const lastAtIndex = textBeforeCursor.lastIndexOf("@");

    const newText =
      textBeforeCursor.substring(0, lastAtIndex) +
      `@${member.displayName} ` +
      textAfterCursor;

    setCommentText(newText);
    setMentionedUsers([...mentionedUsers, member.id]);
    setShowMentionDropdown(false);

    // Focus back to textarea
    setTimeout(() => {
      commentInputRef.current?.focus();
    }, 0);
  };

  // Filter members for mention dropdown
  const filteredMembers = projectMembers.filter(member =>
    member.displayName.toLowerCase().includes(mentionSearch)
  );

  // Handle comment submit
  const handleSubmitComment = async () => {
    if (!commentText.trim()) return;
    try {    
      const response = await commentService.addComment(report.id, {
        content: commentText,
        mentions: Array.from(new Set(mentionedUsers)),
      });
      
      if (response.status === "success" && response.data) {
        setComments([...comments, response.data]);
        setCommentText("");
        setMentionedUsers([]);
        toast.success("Thêm bình luận thành công");
      } else {
        toast.error(response.message || "Không thể thêm bình luận");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi thêm bình luận");
    }
  };

  // Handle comment delete
  const handleDeleteCommentLocal = async (commentId: number) => {
    try {
      const response = await commentService.deleteComment(commentId);
      
      if (response.status === "success") {
        setComments(comments.filter(c => c.id !== commentId));
        toast.success("Xóa bình luận thành công");
      } else {
        toast.error(response.message || "Không thể xóa bình luận");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi xóa bình luận");
    }
  };

  // Handle successful update from Editor
  const handleUpdateSuccess = (updatedReport: import("@/types/report.type").Report) => {
    setIsEditing(false);
    onReportUpdated?.(updatedReport);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
  };

  // Handle report lock toggle
  const handleToggleLock = async () => {
    try {
      const isCurrentlyLocked = report.status === "LOCKED";
      const response = isCurrentlyLocked
        ? await reportService.submitReport(report.id)
        : await reportService.lockReport(report.id);
      
      if (response.status === "success") {
        // Update report status manually since BE returns null data
        const updatedReport = {
          ...report,
          status: isCurrentlyLocked ? "SUBMITTED" : "LOCKED"
        } as Report;
        
        onReportUpdated?.(updatedReport);
        setIsLocked(updatedReport.status === "LOCKED" || isTaskLocked);
        toast.success(
          isCurrentlyLocked
            ? "Đã mở khóa báo cáo thành công"
            : "Đã khóa báo cáo thành công"
        );
      } else {
        toast.error(response.message || "Không thể thay đổi trạng thái khóa");
      }
    } catch (error) {
      toast.error("Đã xảy ra lỗi khi thay đổi trạng thái khóa");
    }
  };

  return (
    <Card className="border-border">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex-1 pb-2">
            {isEditing ? (
              <ProgressReportEditor
                mode="edit"
                taskId={taskId}
                projectId={projectId}
                milestoneId={milestoneId}
                reportId={report.id}
                initialData={{
                  title: report.title,
                  content: report.content,
                  attachments: report.attachments,
                }}
                onSuccess={handleUpdateSuccess}
                onCancel={handleCancelEdit}
              />
            ) : (
              <>
                {/* Top: Reporter Information & Created Time */}
                <div className="flex items-center gap-2 mb-3">
                  <Avatar className="w-6 h-6">
                    <AvatarFallback className="text-xs bg-primary text-primary-foreground">
                      {getInitials(report.reporter.displayName)}
                    </AvatarFallback>
                  </Avatar>
                  <span className="text-sm font-medium text-foreground">{report.reporter.displayName}</span>
                  <span className="text-sm text-muted-foreground">•</span>
                  <span className="text-sm text-muted-foreground">
                    {report.createdAt.toLocaleString("vi-VN")}
                  </span>
                  {isLocked && (
                    <Badge variant="outline" className="bg-destructive/10 text-destructive border-destructive/20 ml-2">
                      <Lock className="w-3 h-3" />
                    </Badge>
                  )}
                </div>

                {/* Mid: Title & Content */}
                <h3 className="font-semibold text-lg text-foreground mb-2">{report.title}</h3>
                {!isExpanded && (
                  <p className="text-sm text-muted-foreground line-clamp-2">
                    {report.content}
                  </p>
                )}

                {/* Bottom: Attachment Counts */}
                {!isExpanded && report.attachments.length > 0 && (
                  <div className="mt-2 flex items-center gap-1 text-xs text-muted-foreground">
                    <FileText className="w-3 h-3" />
                    <span>{report.attachments.length} tệp đính kèm</span>
                  </div>
                )}
              </>
            )}
          </div>

          {!isEditing && (
            <div className="flex items-center gap-1">
              {canEdit && (
                <Button
                  size="icon"
                  variant="ghost"
                  className="h-7 w-7"
                  onClick={() => setIsEditing(true)}
                >
                  <Edit className="w-3 h-3" />
                </Button>
              )}
              {canLock && (
                <Button
                  size="icon"
                  variant="ghost"
                  className="h-7 w-7"
                  onClick={handleToggleLock}
                >
                  {isLocked ? <Unlock className="w-3 h-3" /> : <Lock className="w-3 h-3" />}
                </Button>
              )}
              <Button
                size="icon"
                variant="ghost"
                className="h-7 w-7"
                onClick={handleToggleExpand}
              >
                {isExpanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
              </Button>
            </div>
          )}
        </div>
      </CardHeader>

      {isExpanded && !isEditing && (
        <CardContent className="space-y-4">
          {/* Report Content */}
          <div>
            <p className="text-sm text-foreground whitespace-pre-wrap">{report.content}</p>
          </div>

          {/* Attachments */}
          {report.attachments.length > 0 && (
            <div className="space-y-2">
              <h4 className="text-sm font-medium text-muted-foreground flex items-center gap-2">
                <FileText className="w-4 h-4" />
                Tệp đính kèm ({report.attachments.length})
              </h4>
              <div className="space-y-2">
                {report.attachments.map((attachment) => (
                  <FileCard
                    key={attachment.id}
                    fileName={attachment.fileName}
                    fileSize={attachment.fileSize}
                    downloadUrl={attachment.url}
                    variant="existing"
                  />
                ))}
              </div>
            </div>
          )}

          {/* Comments Section */}
          <div className="space-y-3 border-t border-border pt-4">
            <h4 className="text-sm font-medium text-foreground">
              Bình luận ({comments.length})
            </h4>

            {isLoadingComments ? (
              <div className="text-center py-4 text-muted-foreground text-sm">
                Đang tải bình luận...
              </div>
            ) : (
              /* Existing Comments */
              <div className="space-y-3">
                {comments.map((comment) => (
                  <div key={comment.id} className="flex gap-2">
                    <Avatar className="w-7 h-7">
                      <AvatarFallback className="text-xs bg-primary text-primary-foreground">
                        {getInitials(comment.commenter.displayName)}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-medium text-foreground">
                          {comment.commenter.displayName}
                        </span>
                        <span className="text-xs text-muted-foreground">
                          {comment.createdDate.toLocaleString("vi-VN")}
                        </span>
                      </div>
                      <div className="flex items-start justify-between gap-2">
                        <p className="text-sm text-foreground whitespace-pre-wrap">
                          {comment.content}
                        </p>
                        {!isLocked && user?.id === comment.commenter.id && (
                          <Button
                            size="icon"
                            variant="ghost"
                            className="h-6 w-6 text-destructive hover:text-destructive"
                            onClick={() => handleDeleteCommentLocal(comment.id)}
                          >
                            <Trash className="w-3 h-3" />
                          </Button>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Add Comment */}
            {!isLocked && (
              <div className="space-y-2 relative">
                <Textarea
                  ref={commentInputRef}
                  placeholder="Thêm bình luận... (Gõ @ để tag thành viên)"
                  value={commentText}
                  onChange={handleCommentChange}
                  rows={2}
                  className="bg-white"
                />

                {/* Mention Dropdown */}
                {showMentionDropdown && filteredMembers.length > 0 && (
                  <div className="absolute z-10 w-full max-h-48 overflow-y-auto bg-white border border-border rounded-md shadow-lg">
                    {filteredMembers.map((member) => (
                      <button
                        key={member.id}
                        type="button"
                        className="w-full flex items-center gap-2 px-3 py-2 hover:bg-secondary text-left"
                        onClick={() => insertMention(member)}
                      >
                      <Avatar className="w-6 h-6">
                        <AvatarFallback className="text-xs">
                          {getInitials(member.displayName)}
                        </AvatarFallback>
                      </Avatar>
                        <span className="text-sm text-foreground">{member.displayName}</span>
                        {member.role === "INSTRUCTOR" && (
                          <Badge variant="outline" className="text-xs ml-auto">
                            Giảng viên
                          </Badge>
                        )}
                      </button>
                    ))}
                  </div>
                )}

                <Button
                  size="sm"
                  onClick={handleSubmitComment}
                  disabled={!commentText.trim()}
                  className="cursor-pointer"
                >
                  <Send className="w-3 h-3 mr-2" />
                  Gửi
                </Button>
              </div>
            )}
          </div>
        </CardContent>
      )}
    </Card>
  );
};
