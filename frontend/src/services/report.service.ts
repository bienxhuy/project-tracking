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

/**
 * Helper function to parse date strings to Date objects in Report
 */
function parseReportDates<T extends Report | ReportDetail>(report: any): T {
  return {
    ...report,
    submittedAt: new Date(report.submittedAt),
  };
}

class ReportService {
  /**
   * Get all reports for a task
   */
  async getReportsByTask(taskId: number): Promise<ApiResponse<Report[]>> {
    const response = await apiClient.get<ApiResponse<Report[]>>(
      `/api/v1/tasks/${taskId}/reports`
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = response.data.data.map(report => parseReportDates<Report>(report));
    }
    return response.data;
  }

  /**
   * Get report detail by ID
   */
  async getReportById(reportId: number): Promise<ApiResponse<ReportDetail>> {
    const response = await apiClient.get<ApiResponse<ReportDetail>>(
      `/api/v1/reports/${reportId}`
    );
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseReportDates<ReportDetail>(response.data.data);
    }
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
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseReportDates<Report>(response.data.data);
    }
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
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseReportDates<Report>(response.data.data);
    }
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
    if (response.data.status === "success" && response.data.data) {
      response.data.data = parseReportDates<Report>(response.data.data);
    }
    return response.data;
  }
}

export const reportService = new ReportService();
