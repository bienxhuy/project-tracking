package POSE_Project_Tracking.Blog.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import POSE_Project_Tracking.Blog.dto.res.AssignedUserRes;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.dto.res.TaskRes;
import POSE_Project_Tracking.Blog.service.IPdfExportService;
import POSE_Project_Tracking.Blog.service.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfExportServiceImpl implements IPdfExportService {

    private final IProjectService projectService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Colors
    private static final Color PRIMARY_COLOR = new DeviceRgb(37, 99, 235); // Blue
    private static final Color SUCCESS_COLOR = new DeviceRgb(34, 197, 94); // Green
    private static final Color WARNING_COLOR = new DeviceRgb(234, 179, 8); // Yellow
    private static final Color DANGER_COLOR = new DeviceRgb(239, 68, 68); // Red
    private static final Color GRAY_COLOR = new DeviceRgb(156, 163, 175); // Gray

    @Override
    public byte[] exportProjectToPdf(Long projectId) throws IOException {
        log.info("Starting PDF export for project ID: {}", projectId);
        
        try {
            // Get project with full details
            ProjectRes project = projectService.getProjectWithDetails(projectId);
            log.info("Retrieved project: {}", project.getTitle());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            try {
                // Use standard font (WinAnsi encoding for basic Latin characters)
                PdfFont font = PdfFontFactory.createFont("Helvetica");
                document.setFont(font);
                log.debug("Font created successfully");

                // Add content
                addHeader(document, project);
                addProjectInfo(document, project);
                addProgressSection(document, project);
                addMembersSection(document, project);
                addMilestonesSection(document, project);
                addTasksSection(document, project);
                addFooter(document);
                log.debug("All PDF sections added successfully");

                document.close();
                byte[] pdfBytes = baos.toByteArray();
                log.info("PDF generated successfully. Size: {} bytes", pdfBytes.length);
                return pdfBytes;

            } catch (Exception e) {
                log.error("Error during PDF generation", e);
                document.close();
                throw new IOException("Error generating PDF: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error exporting project {} to PDF", projectId, e);
            throw new IOException("Failed to export project to PDF: " + e.getMessage(), e);
        }
    }

    private void addHeader(Document document, ProjectRes project) {
        Paragraph title = new Paragraph("PROJECT REPORT")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(5);
        document.add(title);

        Paragraph subtitle = new Paragraph(project.getTitle())
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);

        // Horizontal line
        document.add(new LineSeparator(new SolidLine()));
        document.add(new Paragraph().setMarginBottom(10));
    }

    private void addProjectInfo(Document document, ProjectRes project) {
        document.add(new Paragraph("PROJECT INFORMATION")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .useAllAvailableWidth();

        addInfoRow(table, "Title:", project.getTitle());
        addInfoRow(table, "Year:", project.getYear() != null ? project.getYear().toString() : "N/A");
        addInfoRow(table, "Semester:", project.getSemester() != null ? project.getSemester().toString() : "N/A");
        addInfoRow(table, "Faculty:", project.getFaculty() != null ? project.getFaculty() : "N/A");
        addInfoRow(table, "Batch:", project.getBatch() != null ? project.getBatch() : "N/A");
        addInfoRow(table, "Status:", getStatusText(project.getStatus().name()));
        addInfoRow(table, "Instructor:", project.getInstructorName() != null ? project.getInstructorName() : "N/A");
        addInfoRow(table, "Start Date:", project.getStartDate() != null ? project.getStartDate().format(DATE_FORMATTER) : "N/A");
        addInfoRow(table, "End Date:", project.getEndDate() != null ? project.getEndDate().format(DATE_FORMATTER) : "N/A");

        document.add(table);
        document.add(new Paragraph().setMarginBottom(10));

        // Objectives
        if (project.getObjectives() != null && !project.getObjectives().isEmpty()) {
            document.add(new Paragraph("Objectives:")
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(5));
            document.add(new Paragraph(project.getObjectives())
                    .setMarginBottom(10));
        }

        // Content/Description
        if (project.getContent() != null && !project.getContent().isEmpty()) {
            document.add(new Paragraph("Description:")
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(5));
            document.add(new Paragraph(project.getContent())
                    .setMarginBottom(15));
        }
    }

    private void addProgressSection(Document document, ProjectRes project) {
        document.add(new Paragraph("PROGRESS OVERVIEW")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10));

        Float completion = project.getCompletionPercentage() != null ? project.getCompletionPercentage() : 0f;
        
        // Progress bar
        Table progressTable = new Table(UnitValue.createPercentArray(new float[]{80, 20}))
                .useAllAvailableWidth();

        // Progress bar cell
        Cell progressCell = new Cell()
                .setBackgroundColor(new DeviceRgb(229, 231, 235))
                .setPadding(0)
                .setBorder(null);

        // Filled portion
        if (completion > 0) {
            Table innerTable = new Table(UnitValue.createPercentArray(new float[]{completion, 100 - completion}))
                    .useAllAvailableWidth();
            
            innerTable.addCell(new Cell()
                    .add(new Paragraph(""))
                    .setHeight(20)
                    .setBackgroundColor(getProgressColor(completion))
                    .setBorder(null));
            
            if (completion < 100) {
                innerTable.addCell(new Cell()
                        .add(new Paragraph(""))
                        .setHeight(20)
                        .setBackgroundColor(new DeviceRgb(229, 231, 235))
                        .setBorder(null));
            }
            progressCell.add(innerTable);
        } else {
            progressCell.add(new Paragraph("").setHeight(20));
        }

        progressTable.addCell(progressCell);
        progressTable.addCell(new Cell()
                .add(new Paragraph(String.format("%.1f%%", completion))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(null));

        document.add(progressTable);
        document.add(new Paragraph().setMarginBottom(10));

        // Statistics
        Table statsTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                .useAllAvailableWidth();

        addStatCell(statsTable, "Total Milestones", 
                project.getTotalMilestones() != null ? project.getTotalMilestones().toString() : "0");
        addStatCell(statsTable, "Total Tasks", 
                project.getTotalTasks() != null ? project.getTotalTasks().toString() : "0");
        addStatCell(statsTable, "Total Members", 
                project.getTotalMembers() != null ? project.getTotalMembers().toString() : "0");
        addStatCell(statsTable, "Completion", String.format("%.1f%%", completion));

        document.add(statsTable);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addMembersSection(Document document, ProjectRes project) {
        if (project.getStudents() == null || project.getStudents().isEmpty()) {
            return;
        }

        document.add(new Paragraph("PROJECT MEMBERS")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 40, 35, 15}))
                .useAllAvailableWidth();

        // Header
        addTableHeader(table, "#");
        addTableHeader(table, "Name");
        addTableHeader(table, "Email");
        addTableHeader(table, "Role");

        // Data
        int index = 1;
        for (AssignedUserRes member : project.getStudents()) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(index++))));
            table.addCell(new Cell().add(new Paragraph(member.getDisplayName())));
            table.addCell(new Cell().add(new Paragraph(member.getEmail() != null ? member.getEmail() : "N/A")));
            table.addCell(new Cell().add(new Paragraph(member.getRole() != null ? member.getRole().name() : "N/A")));
        }

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addMilestonesSection(Document document, ProjectRes project) {
        if (project.getMilestones() == null || project.getMilestones().isEmpty()) {
            return;
        }

        document.add(new Paragraph("MILESTONES")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10));

        for (MilestoneRes milestone : project.getMilestones()) {
            // Milestone header
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                    .useAllAvailableWidth();

            headerTable.addCell(new Cell()
                    .add(new Paragraph(milestone.getTitle()).setBold())
                    .setBackgroundColor(new DeviceRgb(243, 244, 246))
                    .setBorder(null));

            Float milestoneCompletion = milestone.getCompletionPercentage() != null ? milestone.getCompletionPercentage() : 0f;
            headerTable.addCell(new Cell()
                    .add(new Paragraph(String.format("Progress: %.1f%%", milestoneCompletion)))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(new DeviceRgb(243, 244, 246))
                    .setBorder(null));

            document.add(headerTable);

            // Milestone details
            Table detailTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .useAllAvailableWidth()
                    .setMarginLeft(10);

            addInfoRow(detailTable, "Status:", getStatusText(milestone.getStatus().name()));
            addInfoRow(detailTable, "Start Date:", milestone.getStartDate() != null ? milestone.getStartDate().format(DATE_FORMATTER) : "N/A");
            addInfoRow(detailTable, "End Date:", milestone.getEndDate() != null ? milestone.getEndDate().format(DATE_FORMATTER) : "N/A");
            
            if (milestone.getDescription() != null && !milestone.getDescription().isEmpty()) {
                addInfoRow(detailTable, "Description:", milestone.getDescription());
            }

            if (milestone.getTasks() != null && !milestone.getTasks().isEmpty()) {
                Cell labelCell = new Cell().add(new Paragraph("Tasks:").setBold()).setBorder(null);
                Cell valueCell = new Cell().setBorder(null);
                
                List taskList = new List()
                        .setSymbolIndent(10)
                        .setListSymbol("\u2022");
                
                for (TaskRes task : milestone.getTasks()) {
                    taskList.add(new ListItem(String.format("%s (%s)", 
                            task.getTitle(), 
                            getStatusText(task.getStatus().name()))));
                }
                valueCell.add(taskList);
                
                detailTable.addCell(labelCell);
                detailTable.addCell(valueCell);
            }

            document.add(detailTable);
            document.add(new Paragraph().setMarginBottom(10));
        }
    }

    private void addTasksSection(Document document, ProjectRes project) {
        if (project.getMilestones() == null || project.getMilestones().isEmpty()) {
            return;
        }

        // Count total tasks
        int taskCount = 0;
        for (MilestoneRes milestone : project.getMilestones()) {
            if (milestone.getTasks() != null) {
                taskCount += milestone.getTasks().size();
            }
        }

        if (taskCount == 0) {
            return;
        }

        document.add(new Paragraph("TASKS SUMMARY")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 30, 20, 15, 15, 15}))
                .useAllAvailableWidth();

        // Header
        addTableHeader(table, "#");
        addTableHeader(table, "Task Title");
        addTableHeader(table, "Milestone");
        addTableHeader(table, "Status");
        addTableHeader(table, "Start Date");
        addTableHeader(table, "End Date");

        // Data
        int index = 1;
        for (MilestoneRes milestone : project.getMilestones()) {
            if (milestone.getTasks() != null) {
                for (TaskRes task : milestone.getTasks()) {
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(index++))));
                    table.addCell(new Cell().add(new Paragraph(task.getTitle())));
                    table.addCell(new Cell().add(new Paragraph(milestone.getTitle())));
                    
                    Cell statusCell = new Cell().add(new Paragraph(getStatusText(task.getStatus().name())));
                    statusCell.setBackgroundColor(getStatusBackgroundColor(task.getStatus().name()));
                    table.addCell(statusCell);
                    
                    table.addCell(new Cell().add(new Paragraph(
                            task.getStartDate() != null ? task.getStartDate().format(DATE_FORMATTER) : "N/A")));
                    table.addCell(new Cell().add(new Paragraph(
                            task.getEndDate() != null ? task.getEndDate().format(DATE_FORMATTER) : "N/A")));
                }
            }
        }

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addFooter(Document document) {
        document.add(new LineSeparator(new SolidLine()));
        
        Paragraph footer = new Paragraph(String.format("Report generated on: %s", 
                LocalDateTime.now().format(DATETIME_FORMATTER)))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(GRAY_COLOR)
                .setMarginTop(10);
        
        document.add(footer);
    }

    // Helper methods
    private void addInfoRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(value)).setBorder(null));
    }

    private void addTableHeader(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(PRIMARY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
    }

    private void addStatCell(Table table, String label, String value) {
        Cell cell = new Cell()
                .add(new Paragraph(label).setFontSize(10).setFontColor(GRAY_COLOR))
                .add(new Paragraph(value).setFontSize(16).setBold())
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new DeviceRgb(249, 250, 251));
        table.addCell(cell);
    }

    private Color getProgressColor(Float percentage) {
        if (percentage >= 80) return SUCCESS_COLOR;
        if (percentage >= 50) return WARNING_COLOR;
        return DANGER_COLOR;
    }

    private Color getStatusBackgroundColor(String status) {
        return switch (status.toUpperCase()) {
            case "COMPLETED", "APPROVED" -> new DeviceRgb(220, 252, 231); // Light green
            case "IN_PROGRESS", "PENDING" -> new DeviceRgb(254, 249, 195); // Light yellow
            case "NOT_STARTED", "REJECTED" -> new DeviceRgb(254, 226, 226); // Light red
            default -> new DeviceRgb(243, 244, 246); // Light gray
        };
    }

    private String getStatusText(String status) {
        return switch (status.toUpperCase()) {
            case "IN_PROGRESS" -> "In Progress";
            case "NOT_STARTED" -> "Not Started";
            case "COMPLETED" -> "Completed";
            case "APPROVED" -> "Approved";
            case "PENDING" -> "Pending";
            case "REJECTED" -> "Rejected";
            default -> status;
        };
    }
}
