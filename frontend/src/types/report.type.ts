// Minimal representation of a progress report data for API calls

import { Attachment } from "./attachment.type";
import { Comment } from "./comment.type";
import { BaseUser } from "./user.type";

export interface Report {
  id: number;
  title: string;
  content: string;
  reporter: BaseUser; 
  attachments: Attachment[];
  status: "SUBMITTED" | "LOCKED";
  createdAt: Date;
}

export interface ReportDetail extends Report {
  comments: Comment[];
}

export interface CreateReportRequest {
  title: string;
  content: string;
  projectId: number;
  milestoneId: number;
  taskId: number;
  attachments: File[];
}

export interface UpdateReportRequest {
  title: string;
  content: string;
  files: File[];
  existingAttachmentIds: number[];
  removedAttachmentIds: number[];
}