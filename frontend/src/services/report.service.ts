// API service for reports

import apiClient from "@/api/axios.customize";
import { ApiResponse } from "@/types/auth.type";
import {
  Report,
  ReportDetail,
  CreateReportRequest,
  UpdateReportRequest,
} from "@/types/report.type";

/**
 * Helper function to parse date strings to Date objects in Report
 * and map BE response to frontend Report type
 */
function parseReportDates<T extends Report | ReportDetail>(report: any): T {
  return {
    id: report.id,
    title: report.title,
    content: report.content,
    status: report.status,
    attachments: report.attachments || [],
    createdAt: new Date(report.createdAt),
    // Computed reporter field from BE submittedById/submittedByName
    reporter: {
      id: report.submittedById,
      displayName: report.submittedByName,
      email: '', // Not provided by BE
      role: 'STUDENT' as const,
    },
    // Include comments if this is ReportDetail
    ...(report.comments !== undefined && { comments: report.comments || [] }),
  } as T;
}

class ReportService {
  /**
   * Get all reports for a task
   */
  async getReportsByTask(taskId: number): Promise<ApiResponse<Report[]>> {
    const response = await apiClient.get<ApiResponse<Report[]>>(
      `/api/v1/reports/task/${taskId}`
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
    data: CreateReportRequest
  ): Promise<ApiResponse<Report>> {
    const formData = new FormData();
    formData.append("title", data.title);
    formData.append("content", data.content);
    formData.append("projectId", data.projectId.toString());
    formData.append("milestoneId", data.milestoneId.toString());
    formData.append("taskId", data.taskId.toString());
    
    // Append multiple files as 'attachments' to match BE expectation
    data.attachments.forEach((file) => {
      formData.append("attachments", file);
    });

    const response = await apiClient.post<ApiResponse<Report>>(
      `/api/v1/reports`,
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
   * Submit report - change status to SUBMITTED (Instructor only)
   */
  async submitReport(
    reportId: number
  ): Promise<ApiResponse<null>> {
    const response = await apiClient.patch<ApiResponse<null>>(
      `/api/v1/reports/${reportId}/submit`
    );
    return response.data;
  }

  /**
   * Lock report - change status to LOCKED (Instructor only)
   */
  async lockReport(
    reportId: number
  ): Promise<ApiResponse<null>> {
    const response = await apiClient.patch<ApiResponse<null>>(
      `/api/v1/reports/${reportId}/lock`
    );
    return response.data;
  }
}

export const reportService = new ReportService();
