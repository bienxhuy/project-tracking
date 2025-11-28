// API service for comments

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import { Comment } from "@/types/comment.type";

class CommentService {
  /**
   * Get all comments for a report
   */
  async getCommentsByReport(reportId: number): Promise<ApiResponse<Comment[]>> {
    const response = await apiClient.get<ApiResponse<Comment[]>>(
      `/api/v1/reports/${reportId}/comments`
    );
    return response.data;
  }

  /**
   * Add a comment to a report
   */
  async addComment(
    reportId: number,
    data: { content: string; mentions: number[] }
  ): Promise<ApiResponse<Comment>> {
    const response = await apiClient.post<ApiResponse<Comment>>(
      `/api/v1/reports/${reportId}/comments`,
      data
    );
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
