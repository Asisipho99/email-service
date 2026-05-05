package com.shopping.email_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {

    @Email
    @NotBlank
    private String to;

    @NotBlank
    private String subject;

    private String body;

    // REGISTRATION, ORDER_CONFIRMATION, STATUS_UPDATE
    @NotBlank
    private String type;

    // Optional extra data for templates
    private String customerName;
    private String orderId;
    private String orderStatus;
    private String productList;
    private Double totalAmount;
}
