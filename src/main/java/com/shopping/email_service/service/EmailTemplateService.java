package com.shopping.email_service.service;

import com.shopping.email_service.dto.EmailRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String buildTemplate(EmailRequest request) {
        return switch (request.getType().toUpperCase()) {
            case "REGISTRATION"        -> buildRegistrationTemplate(request);
            case "ORDER_CONFIRMATION"  -> buildOrderConfirmationTemplate(request);
            case "STATUS_UPDATE"       -> buildStatusUpdateTemplate(request);
            default                    -> buildGenericTemplate(request);
        };
    }

    private String buildRegistrationTemplate(EmailRequest r) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; background:#f4f6f9; padding:20px;">
              <div style="max-width:600px; margin:auto; background:#fff;
                          border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <!-- Header -->
                <div style="background:#1A3A5C; padding:24px; text-align:center;">
                  <h1 style="color:#fff; margin:0; font-size:22px;">
                    Welcome to Our Shopping System!
                  </h1>
                </div>

                <!-- Body -->
                <div style="padding:28px;">
                  <p style="font-size:16px; color:#333;">
                    Hi <strong>%s</strong>,
                  </p>
                  <p style="color:#555; line-height:1.6;">
                    Your account has been successfully created.
                    You can now browse products, place orders, and track deliveries.
                  </p>

                  <div style="background:#D6E4F7; border-radius:6px;
                              padding:16px; margin:20px 0; text-align:center;">
                    <p style="margin:0; color:#1A3A5C; font-weight:bold;">
                      Start Shopping Today!
                    </p>
                  </div>

                  <p style="color:#555;">
                    If you did not create this account, please contact us immediately.
                  </p>
                </div>

                <!-- Footer -->
                <div style="background:#f4f6f9; padding:16px; text-align:center;">
                  <p style="color:#999; font-size:12px; margin:0;">
                    © 2026 Integrated Online Shopping System · TUT Computer Science
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                r.getCustomerName() != null ? r.getCustomerName() : "Customer"
        );
    }

    private String buildOrderConfirmationTemplate(EmailRequest r) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; background:#f4f6f9; padding:20px;">
              <div style="max-width:600px; margin:auto; background:#fff;
                          border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <!-- Header -->
                <div style="background:#1A3A5C; padding:24px; text-align:center;">
                  <h1 style="color:#fff; margin:0; font-size:22px;">
                    Order Confirmed! 🎉
                  </h1>
                </div>

                <!-- Body -->
                <div style="padding:28px;">
                  <p style="font-size:16px; color:#333;">
                    Hi <strong>%s</strong>,
                  </p>
                  <p style="color:#555; line-height:1.6;">
                    Thank you for your order! Here is your order summary:
                  </p>

                  <!-- Order Details -->
                  <div style="border:1px solid #e0e0e0; border-radius:6px; padding:16px; margin:16px 0;">
                    <p style="margin:4px 0; color:#333;">
                      <strong>Order ID:</strong> #%s
                    </p>
                    <p style="margin:4px 0; color:#333;">
                      <strong>Items:</strong> %s
                    </p>
                    <p style="margin:4px 0; color:#1A3A5C; font-size:16px;">
                      <strong>Total: R%.2f</strong>
                    </p>
                  </div>

                  <!-- Status Badge -->
                  <div style="background:#D6E4F7; border-radius:6px;
                              padding:12px; text-align:center;">
                    <p style="margin:0; color:#1A3A5C; font-weight:bold;">
                      Status: PENDING — We are processing your order
                    </p>
                  </div>

                  <p style="color:#555; margin-top:20px;">
                    You will receive another email when your order status changes.
                  </p>
                </div>

                <!-- Footer -->
                <div style="background:#f4f6f9; padding:16px; text-align:center;">
                  <p style="color:#999; font-size:12px; margin:0;">
                    © 2026 Integrated Online Shopping System · TUT Computer Science
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                r.getCustomerName() != null ? r.getCustomerName() : "Customer",
                r.getOrderId()     != null  ? r.getOrderId()     : "N/A",
                r.getProductList() != null  ? r.getProductList() : "See order details",
                r.getTotalAmount() != null  ? r.getTotalAmount() : 0.0
        );
    }

    private String buildStatusUpdateTemplate(EmailRequest r) {
        String badgeColor = switch (
                r.getOrderStatus() != null ? r.getOrderStatus().toUpperCase() : "") {
            case "PROCESSING" -> "#ED7D31";
            case "SHIPPED"    -> "#4472C4";
            case "DELIVERED"  -> "#70AD47";
            case "CANCELLED"  -> "#C0392B";
            default           -> "#1A3A5C";
        };

        return """
            <html>
            <body style="font-family: Arial, sans-serif; background:#f4f6f9; padding:20px;">
              <div style="max-width:600px; margin:auto; background:#fff;
                          border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <!-- Header -->
                <div style="background:#1A3A5C; padding:24px; text-align:center;">
                  <h1 style="color:#fff; margin:0; font-size:22px;">
                    Order Status Update
                  </h1>
                </div>

                <!-- Body -->
                <div style="padding:28px;">
                  <p style="font-size:16px; color:#333;">
                    Hi <strong>%s</strong>,
                  </p>
                  <p style="color:#555; line-height:1.6;">
                    Your order <strong>#%s</strong> has been updated.
                  </p>

                  <!-- Status Badge -->
                  <div style="background:%s; border-radius:6px;
                              padding:16px; text-align:center; margin:20px 0;">
                    <p style="margin:0; color:#fff; font-size:18px; font-weight:bold;">
                      %s
                    </p>
                  </div>

                  <p style="color:#555;">
                    Thank you for shopping with us!
                  </p>
                </div>

                <!-- Footer -->
                <div style="background:#f4f6f9; padding:16px; text-align:center;">
                  <p style="color:#999; font-size:12px; margin:0;">
                    © 2026 Integrated Online Shopping System · TUT Computer Science
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                r.getCustomerName() != null  ? r.getCustomerName()  : "Customer",
                r.getOrderId()      != null  ? r.getOrderId()       : "N/A",
                badgeColor,
                r.getOrderStatus()  != null  ? r.getOrderStatus()   : "UPDATED"
        );
    }

    private String buildGenericTemplate(EmailRequest r) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding:20px;">
              <div style="max-width:600px; margin:auto;">
                <h2 style="color:#1A3A5C;">%s</h2>
                <p style="color:#555;">%s</p>
                <hr/>
                <p style="color:#999; font-size:12px;">
                  © 2026 Integrated Online Shopping System
                </p>
              </div>
            </body>
            </html>
            """.formatted(
                r.getSubject(),
                r.getBody() != null ? r.getBody() : ""
        );
    }
}
