// API service for comments

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import { Comment } from "@/types/comment.type";

/**
 * Parse raw comment data from API to Comment type
 */
function parseComment(rawComment: any): Comment {
  return {
    id: rawComment.id,
    content: rawComment.content,
    createdDate: new Date(rawComment.createdAt),
    commenter: {
      id: rawComment.authorId,
      displayName: rawComment.authorName,
      email: "", // Not provided in response
      role: "STUDENT", // Default, not provided in response
    },
  };
}

class CommentService {
  /**
   * Get all comments for a report
   */
  async getCommentsByReport(reportId: number): Promise<ApiResponse<Comment[]>> {
    const response = await apiClient.get<ApiResponse<any[]>>(
      `/api/v1/comments/report/${reportId}`
    );
    
    // Parse response to fit Comment type
    if (response.data.status === "success" && response.data.data) {
      response.data.data = response.data.data.map((rawComment: any) => 
        parseComment(rawComment)
      );
    }
    
    return response.data;
  }

  /**
   * Add a comment to a report
   */
  async addComment(
    reportId: number,
    data: { content: string; mentions: number[] }
  ): Promise<ApiResponse<Comment>> {
    const response = await apiClient.post<ApiResponse<any>>(
      `/api/v1/comments`,
      {
        content: data.content,
        reportId: reportId,
        mentions: data.mentions,
      }
    );
    
    // Parse response to fit Comment type
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseComment(response.data.data);
    }
    
    return response.data;
  }

  /**
   * Delete a comment (own comment only)
   */
  async deleteComment(commentId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete<ApiResponse<void>>(
      `/api/v1/comments/${commentId}`
    );
    return response.data;
  }
}

export const commentService = new CommentService();
