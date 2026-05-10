package com.shopping.email_service.service;

import com.shopping.email_service.dto.EmailRequest;
import com.shopping.email_service.dto.EmailResponse;
import com.shopping.email_service.entity.EmailLog;
import com.shopping.email_service.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailLogRepository   emailLogRepository;
    private final EmailTemplateService templateService;
    private final RestTemplate         restTemplate;

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${resend.api-url}")
    private String resendApiUrl;

    @Value("${app.email.from}")
    private String fromAddress;

    @Value("${app.email.sender-name}")
    private String senderName;

    public EmailResponse sendEmail(EmailRequest request) {
        EmailLog log = new EmailLog();
        log.setRecipient(request.getTo());
        log.setSubject(request.getSubject());
        log.setEmailType(request.getType());

        try {
            String htmlContent = templateService.buildTemplate(request);

            // Build Resend API payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("from",    senderName + " <" + fromAddress + ">");
            payload.put("to",      new String[]{ request.getTo() });
            payload.put("subject", request.getSubject());
            payload.put("html",    htmlContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            ResponseEntity<Map> response = restTemplate.exchange(
                    resendApiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Resend API returned: " + response.getStatusCode());
            }

            log.setStatus("SENT");
            emailLogRepository.save(log);

            return new EmailResponse(true, "Email sent successfully",
                    request.getTo(), request.getType());

        } catch (Exception e) {
            String reason = e.getClass().getSimpleName() + ": " + e.getMessage();
            log.setStatus("FAILED");
            log.setFailureReason(reason);
            emailLogRepository.save(log);

            System.err.println("Email send FAILED to " + request.getTo()
                    + " | type=" + request.getType()
                    + " | reason=" + reason);

            return new EmailResponse(false, "Email failed: " + reason,
                    request.getTo(), request.getType());
        }
    }

    public List<EmailLog> getAllLogs() {
        return emailLogRepository.findAll();
    }

    public List<EmailLog> getLogsByRecipient(String email) {
        return emailLogRepository.findByRecipient(email);
    }
}
