// API service for reports

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import {
  Report,
  ReportDetail,
  CreateReportRequest,
  UpdateReportRequest,
  ToggleReportLockRequest,
} from "@/types/report.type";

class ReportService {
  /**
   * Get all reports for a task
   */
  async getReportsByTask(taskId: number): Promise<ApiResponse<Report[]>> {
    const response = await apiClient.get<ApiResponse<Report[]>>(
      `/api/v1/tasks/${taskId}/reports`
    );
    return response.data;
  }

  /**
   * Get report detail by ID
   */
  async getReportById(reportId: number): Promise<ApiResponse<ReportDetail>> {
    const response = await apiClient.get<ApiResponse<ReportDetail>>(
      `/api/v1/reports/${reportId}`
    );
    return response.data;
  }

  /**
   * Create a new progress report with file uploads
   */
  async createReport(
    taskId: number,
    data: CreateReportRequest
  ): Promise<ApiResponse<Report>> {
    const formData = new FormData();
    formData.append("title", data.title);
    formData.append("content", data.content);
    
    // Append multiple files
    data.files.forEach((file) => {
      formData.append("files", file);
    });

    const response = await apiClient.post<ApiResponse<Report>>(
      `/api/v1/tasks/${taskId}/reports`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  }

  /**
   * Update an existing report
   */
  async updateReport(
    reportId: number,
    data: UpdateReportRequest
  ): Promise<ApiResponse<Report>> {
    const formData = new FormData();
    formData.append("title", data.title);
    formData.append("content", data.content);
    
    // Append new files
    data.files.forEach((file) => {
      formData.append("files", file);
    });
    
    // Append existing attachment IDs
    data.existingAttachmentIds.forEach((id) => {
      formData.append("existingAttachmentIds", id.toString());
    });
    
    // Append removed attachment IDs
    data.removedAttachmentIds.forEach((id) => {
      formData.append("removedAttachmentIds", id.toString());
    });

    const response = await apiClient.put<ApiResponse<Report>>(
      `/api/v1/reports/${reportId}`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  }

  /**
   * Toggle report lock status (Instructor only)
   */
  async toggleReportLock(
    reportId: number,
    data: ToggleReportLockRequest
  ): Promise<ApiResponse<Report>> {
    const response = await apiClient.patch<ApiResponse<Report>>(
      `/api/v1/reports/${reportId}/lock`,
      data
    );
    return response.data;
  }
}

export const reportService = new ReportService();
