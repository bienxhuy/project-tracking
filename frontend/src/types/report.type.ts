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