package OOSE_Final_Project.Blog.service;

import OOSE_Final_Project.Blog.dto.res.MessageDTO;

public interface IEmailService {
    void sendEmail(MessageDTO messageDTO);
}
