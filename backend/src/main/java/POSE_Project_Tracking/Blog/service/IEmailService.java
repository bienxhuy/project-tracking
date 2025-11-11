package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.res.MessageDTO;

public interface IEmailService {
    void sendEmail(MessageDTO messageDTO);
}
