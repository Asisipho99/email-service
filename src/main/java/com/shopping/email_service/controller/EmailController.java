package com.shopping.email_service.controller;

import com.shopping.email_service.dto.EmailRequest;
import com.shopping.email_service.dto.EmailResponse;
import com.shopping.email_service.entity.EmailLog;
import com.shopping.email_service.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;

    // POST /api/email/send — send any email
    @PostMapping("/send")
    public ResponseEntity<EmailResponse> sendEmail(
            @Valid @RequestBody EmailRequest request) {
        return ResponseEntity.ok(emailService.sendEmail(request));
    }

    // GET /api/email/logs — get all email logs (admin)
    @GetMapping("/logs")
    public ResponseEntity<List<EmailLog>> getLogs() {
        return ResponseEntity.ok(emailService.getAllLogs());
    }

    // GET /api/email/logs/{email} — get logs for a recipient
    @GetMapping("/logs/{email}")
    public ResponseEntity<List<EmailLog>> getLogsByRecipient(
            @PathVariable String email) {
        return ResponseEntity.ok(emailService.getLogsByRecipient(email));
    }
}
