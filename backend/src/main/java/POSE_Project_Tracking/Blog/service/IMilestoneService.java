package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.MilestoneReq;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;

import java.util.List;

public interface IMilestoneService {
    MilestoneRes createMilestone(MilestoneReq milestoneReq);
    MilestoneRes updateMilestone(Long id, MilestoneReq milestoneReq);
    MilestoneRes getMilestoneById(Long id, String include);
    MilestoneRes getMilestoneWithDetails(Long id);
    List<MilestoneRes> getAllMilestones();
    List<MilestoneRes> getMilestonesByProject(Long projectId, String include);
    List<MilestoneRes> getMilestonesByStatus(EMilestoneStatus status);
    List<MilestoneRes> getOverdueMilestones();
    void deleteMilestone(Long id);
    MilestoneRes toggleMilestoneLock(Long id, Boolean isLocked);
    void updateMilestoneCompletion(Long id);
    void updateMilestoneStatus(Long id, EMilestoneStatus status);
}
