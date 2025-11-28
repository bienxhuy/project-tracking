package POSE_Project_Tracking.Blog.service.impl;

import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import POSE_Project_Tracking.Blog.dto.res.MessageDTO;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl service;

    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        messageDTO = MessageDTO.builder()
                .to("test@example.com")
                .toName("Test User")
                .subject("Test Subject")
                .from("noreply@example.com")
                .OTP("123456")
                .build();
    }

    @Test
    void sendEmail_validMessage_sendsEmail() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html>Test</html>");

        service.sendEmail(messageDTO);

        verify(javaMailSender).send(mimeMessage);
        verify(templateEngine).process(any(String.class), any(Context.class));
    }
}

