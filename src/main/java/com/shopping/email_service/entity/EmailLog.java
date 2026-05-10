package com.shopping.email_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
@Data
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String emailType;

    @Column(nullable = false)
    private String status; // SENT or FAILED

    @Column(columnDefinition = "TEXT")
    private String failureReason; // populated when status = FAILED

    private LocalDateTime sentAt = LocalDateTime.now();
}