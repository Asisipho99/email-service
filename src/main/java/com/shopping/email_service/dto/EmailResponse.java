package com.shopping.email_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailResponse {
    private boolean success;
    private String message;
    private String recipient;
    private String type;
}