package POSE_Project_Tracking.Blog.util;

import java.time.LocalDate;

/**
 * Utility class for calculating academic year, semester, and batch information
 */
public class AcademicYearUtil {

    /**
     * Get current academic year based on current date
     * Year changes in January (start of semester 1)
     */
    public static Integer getCurrentYear() {
        LocalDate now = LocalDate.now();
        return now.getYear();
    }

    /**
     * Get current semester based on current date
     * Semester 1: January to June (months 1-6)
     * Semester 2: July to December (months 7-12)
     */
    public static Integer getCurrentSemester() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        
        if (month >= 1 && month <= 6) {
            return 2; // Semester 1
        } else {
            return 1; // Semester 2
        }
    }

    /**
     * Get current batch based on current date
     * Each semester is divided into 2 batches
     * 
     * Semester 1 (Jan-Jun):
     *   - Batch 1: January to March (months 1-3)
     *   - Batch 2: April to June (months 4-6)
     * 
     * Semester 2 (Jul-Dec):
     *   - Batch 1: July to September (months 7-9)
     *   - Batch 2: October to December (months 10-12)
     */
    public static String getCurrentBatch() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        
        if (month >= 1 && month <= 3) {
            return "1"; // Semester 1 - Batch 1
        } else if (month >= 4 && month <= 6) {
            return "2"; // Semester 1 - Batch 2
        } else if (month >= 7 && month <= 9) {
            return "1"; // Semester 2 - Batch 1
        } else {
            return "2"; // Semester 2 - Batch 2
        }
    }

    /**
     * Get formatted batch string with year and semester context
     * Format: {year}-S{semester}-B{batch}
     * Example: "2025-S1-B1" for year 2025, semester 1, batch 1
     */
    public static String getFormattedCurrentBatch() {
        return String.format("%d-S%d-B%s", 
            getCurrentYear(), 
            getCurrentSemester(), 
            getCurrentBatch());
    }

    /**
     * Validate semester value
     * @param semester semester number (1 or 2)
     * @return true if valid, false otherwise
     */
    public static boolean isValidSemester(Integer semester) {
        return semester != null && (semester == 1 || semester == 2);
    }

    /**
     * Validate batch value
     * @param batch batch string ("1" or "2")
     * @return true if valid, false otherwise
     */
    public static boolean isValidBatch(String batch) {
        return batch != null && (batch.equals("1") || batch.equals("2"));
    }
}
