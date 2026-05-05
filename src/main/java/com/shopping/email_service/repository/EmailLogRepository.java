package com.shopping.email_service.repository;

import com.shopping.email_service.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByRecipient(String recipient);
    List<EmailLog> findByStatus(String status);
    List<EmailLog> findByEmailType(String emailType);
}