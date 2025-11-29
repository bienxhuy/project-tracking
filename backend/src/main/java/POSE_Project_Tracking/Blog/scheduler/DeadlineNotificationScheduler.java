package POSE_Project_Tracking.Blog.scheduler;

import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.service.NotificationHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler để kiểm tra và gửi thông báo deadline
 * Chạy mỗi ngày vào lúc 8:00 sáng
 */
@Slf4j
@Component
public class DeadlineNotificationScheduler {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationHelperService notificationHelperService;

    /**
     * Kiểm tra các project/milestone/task sắp hết hạn (còn 1 ngày)
     * Chạy mỗi ngày vào lúc 8:00 sáng
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkDeadlineApproaching() {
        log.info("Starting deadline notification check...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();

        // Kiểm tra Projects
        checkProjectDeadlines(tomorrow, today);

        // Kiểm tra Milestones
        checkMilestoneDeadlines(tomorrow, today);

        // Kiểm tra Tasks
        checkTaskDeadlines(tomorrow, today);

        log.info("Deadline notification check completed.");
    }

    /**
     * Kiểm tra Project deadline
     */
    private void checkProjectDeadlines(LocalDate tomorrow, LocalDate today) {
        try {
            List<Project> projects = projectRepository.findByEndDate(tomorrow);

            for (Project project : projects) {
                // Chỉ thông báo nếu project chưa hoàn thành
                if (project.getStatus() != EProjectStatus.COMPLETED) {
                    String title = "Dự án sắp hết hạn";
                    String message = String.format(
                        "Dự án \"%s\" sẽ hết hạn vào ngày mai (%s)", 
                        project.getTitle(), 
                        tomorrow
                    );

                    notificationHelperService.createNotificationsForAllProjectMembers(
                        project,
                        title,
                        message,
                        ENotificationType.PROJECT_DEADLINE_APPROACHING,
                        project.getId(),
                        "PROJECT",
                        null // System notification
                    );

                    log.info("Sent deadline notification for project: {}", project.getTitle());
                }
            }
        } catch (Exception e) {
            log.error("Error checking project deadlines: {}", e.getMessage());
        }
    }

    /**
     * Kiểm tra Milestone deadline
     */
    private void checkMilestoneDeadlines(LocalDate tomorrow, LocalDate today) {
        try {
            List<Milestone> milestones = milestoneRepository.findByEndDate(tomorrow);

            for (Milestone milestone : milestones) {
                // Chỉ thông báo nếu milestone chưa hoàn thành
                if (milestone.getStatus() != EMilestoneStatus.COMPLETED) {
                    String title = "Milestone sắp hết hạn";
                    String message = String.format(
                        "Milestone \"%s\" trong dự án \"%s\" sẽ hết hạn vào ngày mai (%s)", 
                        milestone.getTitle(),
                        milestone.getProject().getTitle(),
                        tomorrow
                    );

                    notificationHelperService.createNotificationsForAllProjectMembers(
                        milestone.getProject(),
                        title,
                        message,
                        ENotificationType.MILESTONE_DEADLINE_APPROACHING,
                        milestone.getId(),
                        "MILESTONE",
                        null // System notification
                    );

                    log.info("Sent deadline notification for milestone: {}", milestone.getTitle());
                }
            }
        } catch (Exception e) {
            log.error("Error checking milestone deadlines: {}", e.getMessage());
        }
    }

    /**
     * Kiểm tra Task deadline
     */
    private void checkTaskDeadlines(LocalDate tomorrow, LocalDate today) {
        try {
            List<Task> tasks = taskRepository.findByEndDate(tomorrow);

            for (Task task : tasks) {
                // Chỉ thông báo nếu task chưa hoàn thành
                if (task.getStatus() != ETaskStatus.COMPLETED) {
                    String title = "Nhiệm vụ sắp hết hạn";
                    String message = String.format(
                        "Nhiệm vụ \"%s\" trong dự án \"%s\" sẽ hết hạn vào ngày mai (%s)", 
                        task.getTitle(),
                        task.getProject().getTitle(),
                        tomorrow
                    );

                    notificationHelperService.createNotificationsForAllProjectMembers(
                        task.getProject(),
                        title,
                        message,
                        ENotificationType.TASK_DEADLINE_APPROACHING,
                        task.getId(),
                        "TASK",
                        null // System notification
                    );

                    log.info("Sent deadline notification for task: {}", task.getTitle());
                }
            }
        } catch (Exception e) {
            log.error("Error checking task deadlines: {}", e.getMessage());
        }
    }
}
