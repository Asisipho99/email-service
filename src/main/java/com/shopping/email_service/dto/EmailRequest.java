package com.shopping.email_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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

    // Purchased lines, rendered as rows in the ORDER_CONFIRMATION email.
    // Optional: when null or empty the template falls back to productList.
    private List<OrderItemLine> items;

    // One purchased line, rendered as a row in the ORDER_CONFIRMATION email.
    // Wire-compatible with com.shopping.integration_service.dto.EmailRequest.OrderItemLine.
    @Data
    public static class OrderItemLine {
        private String     productName;
        private Integer    quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private String     imageUrl;   // absolute https Cloudinary URL, or null
    }
}
