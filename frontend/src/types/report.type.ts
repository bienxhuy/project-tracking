// Minimal representation of a progress report data for API calls

import { Attachment } from "./attachment.type";
import { Comment } from "./comment.type";

export interface Report {
  id: number;
  title: string;
  content: string;
  attachments: Attachment[];
  status: "SUBMITTED" | "LOCKED";
}

export interface ReportDetail extends Report {
  comments: Comment[];
}

export interface CreateReportRequest {
  title: string;
  content: string;
  files: File[];
}

export interface UpdateReportRequest {
  title: string;
  content: string;
  files: File[];
  existingAttachmentIds: number[];
  removedAttachmentIds: number[];
}

export interface ToggleReportLockRequest {
  isLocked: boolean;
}