package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.MilestoneReq;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;

import java.util.List;

public interface IMilestoneService {
    MilestoneRes createMilestone(MilestoneReq milestoneReq);
    MilestoneRes updateMilestone(Long id, MilestoneReq milestoneReq);
    MilestoneRes getMilestoneById(Long id);
    MilestoneRes getMilestoneWithDetails(Long id);
    List<MilestoneRes> getAllMilestones();
    List<MilestoneRes> getMilestonesByProject(Long projectId);
    List<MilestoneRes> getMilestonesByStatus(EMilestoneStatus status);
    List<MilestoneRes> getOverdueMilestones();
    void deleteMilestone(Long id);
    void lockMilestone(Long id, Long userId);
    void unlockMilestone(Long id);
    void updateMilestoneCompletion(Long id);
    void updateMilestoneStatus(Long id, EMilestoneStatus status);
}
