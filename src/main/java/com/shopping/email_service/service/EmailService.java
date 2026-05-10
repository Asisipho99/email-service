package com.shopping.email_service.service;

import com.shopping.email_service.dto.EmailRequest;
import com.shopping.email_service.dto.EmailResponse;
import com.shopping.email_service.entity.EmailLog;
import com.shopping.email_service.repository.EmailLogRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender       mailSender;
    private final EmailLogRepository   emailLogRepository;
    private final EmailTemplateService templateService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.email.sender-name}")
    private String senderName;

    public EmailResponse sendEmail(EmailRequest request) {
        EmailLog log = new EmailLog();
        log.setRecipient(request.getTo());
        log.setSubject(request.getSubject());
        log.setEmailType(request.getType());

        try {
            // Build HTML email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, senderName);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());

            // Use HTML template
            String htmlContent = templateService.buildTemplate(request);
            helper.setText(htmlContent, true); // true = isHtml

            mailSender.send(message);

            // Log success
            log.setStatus("SENT");
            emailLogRepository.save(log);

            return new EmailResponse(
                    true,
                    "Email sent successfully",
                    request.getTo(),
                    request.getType()
            );

        } catch (Exception e) {
            // Log failure with reason so it's visible in the email_logs table
            String reason = e.getClass().getSimpleName() + ": " + e.getMessage();
            log.setStatus("FAILED");
            log.setFailureReason(reason);
            emailLogRepository.save(log);

            // Also print to console so it appears in Railway logs
            System.err.println("Email send FAILED to " + request.getTo()
                    + " | type=" + request.getType()
                    + " | reason=" + reason);

            return new EmailResponse(
                    false,
                    "Email failed: " + reason,
                    request.getTo(),
                    request.getType()
            );
        }
    }

    // Get all email logs
    public List<EmailLog> getAllLogs() {
        return emailLogRepository.findAll();
    }

    // Get logs for a specific recipient
    public List<EmailLog> getLogsByRecipient(String email) {
        return emailLogRepository.findByRecipient(email);
    }
}
