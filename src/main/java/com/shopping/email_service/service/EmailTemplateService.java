package com.shopping.email_service.service;

import com.shopping.email_service.dto.EmailRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class EmailTemplateService {

    public String buildTemplate(EmailRequest request) {
        String type = request.getType() != null ? request.getType().toUpperCase() : "";
        return switch (type) {
            case "REGISTRATION"       -> buildRegistrationTemplate(request);
            case "ORDER_CONFIRMATION" -> buildOrderConfirmationTemplate(request);
            case "STATUS_UPDATE"      -> buildStatusUpdateTemplate(request);
            default                   -> buildGenericTemplate(request);
        };
    }

    // -------------------------------------------------------------------------
    // REGISTRATION
    // -------------------------------------------------------------------------
    private String buildRegistrationTemplate(EmailRequest r) {
        String name = r.getCustomerName() != null ? r.getCustomerName() : "Customer";
        return "<!DOCTYPE html>" +
            "<html><head>" +
            "<meta charset=\"UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>" +
            "<body style=\"margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;" +
            "background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding:40px 20px;\">" +
            "<tr><td align=\"center\">" +
            "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;background:#ffffff;" +
            "border-radius:16px;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.3);\">" +

            // Header
            "<tr><td style=\"background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);padding:48px 40px;text-align:center;\">" +
            "<div style=\"font-size:56px;margin-bottom:16px;\">&#127881;</div>" +
            "<h1 style=\"color:#ffffff;margin:0;font-size:32px;font-weight:700;letter-spacing:-0.5px;\">Welcome Aboard!</h1>" +
            "<p style=\"color:rgba(255,255,255,0.9);margin:12px 0 0;font-size:16px;\">Your shopping journey starts here</p>" +
            "</td></tr>" +

            // Body
            "<tr><td style=\"padding:48px 40px;\">" +
            "<p style=\"font-size:18px;color:#1a202c;margin:0 0 8px;font-weight:600;\">Hi " + name + "!</p>" +
            "<p style=\"color:#4a5568;line-height:1.8;font-size:16px;margin:0 0 32px;\">" +
            "We're thrilled to have you join our community! Your account has been successfully created, " +
            "and you're all set to explore amazing products, place orders, and track your deliveries in real-time." +
            "</p>" +

            // Feature card
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:32px;\">" +
            "<tr><td style=\"padding:24px;background:linear-gradient(135deg,#f093fb 0%,#f5576c 100%);" +
            "border-radius:12px;text-align:center;\">" +
            "<div style=\"font-size:32px;margin-bottom:12px;\">&#128717;</div>" +
            "<h3 style=\"color:#ffffff;margin:0 0 8px;font-size:18px;font-weight:600;\">Browse Products</h3>" +
            "<p style=\"color:rgba(255,255,255,0.9);margin:0;font-size:14px;\">Discover thousands of items</p>" +
            "</td></tr></table>" +

            // CTA Button
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" +
            "<tr><td align=\"center\" style=\"padding:8px 0 32px;\">" +
            "<a href=\"#\" style=\"display:inline-block;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);" +
            "color:#ffffff;text-decoration:none;padding:16px 48px;border-radius:50px;font-weight:600;font-size:16px;" +
            "box-shadow:0 10px 25px rgba(102,126,234,0.4);\">Start Shopping Now</a>" +
            "</td></tr></table>" +

            // Security notice
            "<div style=\"background:#f7fafc;border-left:4px solid #667eea;padding:16px 20px;border-radius:8px;\">" +
            "<p style=\"color:#4a5568;margin:0;font-size:14px;line-height:1.6;\">" +
            "<strong style=\"color:#2d3748;\">&#128274; Security Notice:</strong> " +
            "If you didn't create this account, please contact our support team immediately." +
            "</p></div>" +
            "</td></tr>" +

            // Footer
            "<tr><td style=\"background:#f7fafc;padding:32px 40px;text-align:center;border-top:1px solid #e2e8f0;\">" +
            "<p style=\"color:#718096;font-size:14px;margin:0 0 8px;line-height:1.6;\">Need help? Contact us at " +
            "<a href=\"mailto:support@shopping.com\" style=\"color:#667eea;text-decoration:none;\">support@shopping.com</a></p>" +
            "<p style=\"color:#a0aec0;font-size:12px;margin:0;\">&copy; 2026 Integrated Online Shopping System &middot; TUT Computer Science</p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }

    // -------------------------------------------------------------------------
    // ORDER CONFIRMATION
    // -------------------------------------------------------------------------
    private String buildOrderConfirmationTemplate(EmailRequest r) {
        String name      = r.getCustomerName() != null ? r.getCustomerName()  : "Customer";
        String orderId   = r.getOrderId()      != null ? r.getOrderId()       : "N/A";
        String items     = r.getProductList()  != null ? r.getProductList()   : "See order details";

        // Item rows when the list is populated, otherwise the legacy one-line summary.
        String itemsBlock = (r.getItems() != null && !r.getItems().isEmpty())
            ? "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"margin:0 0 16px;\">" +
              buildItemRows(r.getItems()) +
              "</table>"
            : "<p style=\"margin:0 0 16px;color:#2d3748;font-size:15px;line-height:1.6;\">" + items + "</p>";

        return "<!DOCTYPE html>" +
            "<html><head>" +
            "<meta charset=\"UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>" +
            "<body style=\"margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;" +
            "background:linear-gradient(135deg,#11998e 0%,#38ef7d 100%);\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding:40px 20px;\">" +
            "<tr><td align=\"center\">" +
            "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;background:#ffffff;" +
            "border-radius:16px;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.3);\">" +

            // Header
            "<tr><td style=\"background:linear-gradient(135deg,#11998e 0%,#38ef7d 100%);padding:48px 40px;text-align:center;\">" +
            "<div style=\"background:rgba(255,255,255,0.25);width:80px;height:80px;border-radius:50%;" +
            "margin:0 auto 20px;line-height:80px;font-size:48px;\">&#10003;</div>" +
            "<h1 style=\"color:#ffffff;margin:0;font-size:32px;font-weight:700;letter-spacing:-0.5px;\">Order Confirmed!</h1>" +
            "<p style=\"color:rgba(255,255,255,0.9);margin:12px 0 0;font-size:16px;\">Thank you for your purchase</p>" +
            "</td></tr>" +

            // Body
            "<tr><td style=\"padding:48px 40px;\">" +
            "<p style=\"font-size:18px;color:#1a202c;margin:0 0 8px;font-weight:600;\">Hi " + name + "! &#127882;</p>" +
            "<p style=\"color:#4a5568;line-height:1.8;font-size:16px;margin:0 0 32px;\">" +
            "Great news! Your order has been confirmed and we're getting it ready for you. Here's a summary of your purchase:" +
            "</p>" +

            // Order card
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:32px;" +
            "background:linear-gradient(135deg,#f6f8fb 0%,#e9f0f5 100%);border-radius:12px;" +
            "overflow:hidden;border:2px solid #e2e8f0;\">" +
            "<tr><td style=\"padding:24px;\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" +
            "<tr><td style=\"padding-bottom:16px;border-bottom:2px dashed #cbd5e0;\">" +
            "<p style=\"margin:0 0 8px;color:#718096;font-size:13px;text-transform:uppercase;letter-spacing:0.5px;font-weight:600;\">Order ID</p>" +
            "<p style=\"margin:0;color:#2d3748;font-size:20px;font-weight:700;\">#" + orderId + "</p>" +
            "</td></tr>" +
            "<tr><td style=\"padding-top:16px;\">" +
            "<p style=\"margin:0 0 8px;color:#718096;font-size:13px;text-transform:uppercase;letter-spacing:0.5px;font-weight:600;\">Items</p>" +
            itemsBlock +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#ffffff;border-radius:8px;padding:16px;\">" +
            "<tr>" +
            "<td><p style=\"margin:0;color:#718096;font-size:14px;\">Total Amount</p></td>" +
            "<td align=\"right\"><p style=\"margin:0;color:#11998e;font-size:24px;font-weight:700;\">" + formatRand(r.getTotalAmount()) + "</p></td>" +
            "</tr></table>" +
            "</td></tr></table>" +
            "</td></tr></table>" +

            // Status badge
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:32px;\">" +
            "<tr><td style=\"background:linear-gradient(135deg,#ffeaa7 0%,#fdcb6e 100%);padding:20px;border-radius:12px;text-align:center;\">" +
            "<p style=\"margin:0;color:#2d3748;font-size:16px;font-weight:600;\">&#128230; Status: <span style=\"color:#d63031;\">PENDING</span></p>" +
            "<p style=\"margin:8px 0 0;color:#4a5568;font-size:14px;\">We're processing your order right now</p>" +
            "</td></tr></table>" +

            // Info box
            "<div style=\"background:#f7fafc;border-left:4px solid #11998e;padding:16px 20px;border-radius:8px;\">" +
            "<p style=\"color:#4a5568;margin:0;font-size:14px;line-height:1.6;\">" +
            "<strong style=\"color:#2d3748;\">&#128231; Stay Updated:</strong> " +
            "You'll receive another email when your order status changes. Track your order anytime!" +
            "</p></div>" +
            "</td></tr>" +

            // Footer
            "<tr><td style=\"background:#f7fafc;padding:32px 40px;text-align:center;border-top:1px solid #e2e8f0;\">" +
            "<p style=\"color:#718096;font-size:14px;margin:0 0 8px;line-height:1.6;\">Questions? Contact us at " +
            "<a href=\"mailto:support@shopping.com\" style=\"color:#11998e;text-decoration:none;\">support@shopping.com</a></p>" +
            "<p style=\"color:#a0aec0;font-size:12px;margin:0;\">&copy; 2026 Integrated Online Shopping System &middot; TUT Computer Science</p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }

    // -------------------------------------------------------------------------
    // ORDER ITEM ROWS
    // -------------------------------------------------------------------------
    private String buildItemRows(List<EmailRequest.OrderItemLine> items) {
        StringBuilder rows = new StringBuilder();

        for (EmailRequest.OrderItemLine item : items) {
            if (item == null) {
                continue;
            }

            String name = (item.getProductName() != null && !item.getProductName().isBlank())
                ? escapeHtml(item.getProductName())
                : "Item";

            Integer    quantity  = item.getQuantity();
            BigDecimal unitPrice = item.getUnitPrice();
            BigDecimal lineTotal = item.getLineTotal();
            if (lineTotal == null && unitPrice != null && quantity != null) {
                lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            }

            String resizedImage = resizeCloudinaryImage(item.getImageUrl(), 80, 80);
            String imageCell = (resizedImage != null && !resizedImage.isBlank())
                ? "<img src=\"" + resizedImage + "\" alt=\"" + name + "\" width=\"80\" height=\"80\" " +
                  "style=\"display:block;border-radius:8px;border:1px solid #e2e8f0;\">"
                : "&nbsp;";

            String quantityLine = "Qty " + (quantity != null ? quantity : 1);
            if (unitPrice != null) {
                quantityLine += " &times; " + formatRand(unitPrice).replace(" ", "&nbsp;");
            }

            rows.append("<tr>")
                .append("<td width=\"80\" style=\"padding:8px 12px 8px 0;vertical-align:top;width:80px;\">")
                .append(imageCell)
                .append("</td>")
                .append("<td style=\"padding:8px 0;vertical-align:top;\">")
                .append("<p style=\"margin:0 0 4px;color:#2d3748;font-size:15px;font-weight:600;\">")
                .append(name)
                .append("</p>")
                .append("<p style=\"margin:0;color:#718096;font-size:13px;\">")
                .append(quantityLine)
                .append("</p>")
                .append("</td>")
                .append("<td align=\"right\" style=\"padding:8px 0 8px 12px;vertical-align:top;white-space:nowrap;\">")
                .append("<p style=\"margin:0;color:#2d3748;font-size:15px;font-weight:600;\">")
                .append(lineTotal != null ? formatRand(lineTotal) : "&nbsp;")
                .append("</p>")
                .append("</td>")
                .append("</tr>");
        }

        return rows.toString();
    }

    // -------------------------------------------------------------------------
    // STATUS UPDATE
    // -------------------------------------------------------------------------
    private String buildStatusUpdateTemplate(EmailRequest r) {
        String status = r.getOrderStatus() != null ? r.getOrderStatus().toUpperCase() : "UPDATED";
        String name   = r.getCustomerName() != null ? r.getCustomerName() : "Customer";
        String orderId = r.getOrderId()     != null ? r.getOrderId()      : "N/A";

        String gradient;
        String accentColor;
        String statusIcon;
        String statusSubtitle;

        switch (status) {
            case "PROCESSING":
                gradient      = "linear-gradient(135deg,#f093fb 0%,#f5576c 100%)";
                accentColor   = "#f5576c";
                statusIcon    = "&#9881;";
                statusSubtitle = "Your order is being processed";
                break;
            case "SHIPPED":
                gradient      = "linear-gradient(135deg,#4facfe 0%,#00f2fe 100%)";
                accentColor   = "#4facfe";
                statusIcon    = "&#128666;";
                statusSubtitle = "Your order is on its way";
                break;
            case "DELIVERED":
                gradient      = "linear-gradient(135deg,#43e97b 0%,#38f9d7 100%)";
                accentColor   = "#43e97b";
                statusIcon    = "&#10003;";
                statusSubtitle = "Your order has been delivered";
                break;
            case "CANCELLED":
                gradient      = "linear-gradient(135deg,#f5365c 0%,#f56036 100%)";
                accentColor   = "#f5365c";
                statusIcon    = "&#10007;";
                statusSubtitle = "Your order has been cancelled";
                break;
            default:
                gradient      = "linear-gradient(135deg,#667eea 0%,#764ba2 100%)";
                accentColor   = "#667eea";
                statusIcon    = "&#128230;";
                statusSubtitle = "Your order status has been updated";
        }

        return "<!DOCTYPE html>" +
            "<html><head>" +
            "<meta charset=\"UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>" +
            "<body style=\"margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;" +
            "background:" + gradient + ";\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding:40px 20px;\">" +
            "<tr><td align=\"center\">" +
            "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;background:#ffffff;" +
            "border-radius:16px;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.3);\">" +

            // Header
            "<tr><td style=\"background:" + gradient + ";padding:48px 40px;text-align:center;\">" +
            "<div style=\"font-size:64px;margin-bottom:16px;\">" + statusIcon + "</div>" +
            "<h1 style=\"color:#ffffff;margin:0;font-size:32px;font-weight:700;letter-spacing:-0.5px;\">Order Update</h1>" +
            "<p style=\"color:rgba(255,255,255,0.9);margin:12px 0 0;font-size:16px;\">" + statusSubtitle + "</p>" +
            "</td></tr>" +

            // Body
            "<tr><td style=\"padding:48px 40px;\">" +
            "<p style=\"font-size:18px;color:#1a202c;margin:0 0 8px;font-weight:600;\">Hi " + name + "!</p>" +
            "<p style=\"color:#4a5568;line-height:1.8;font-size:16px;margin:0 0 32px;\">" +
            "We have an update on your order. Here's the latest status:" +
            "</p>" +

            // Order info card
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:24px;" +
            "background:#f7fafc;border-radius:12px;border:2px solid #e2e8f0;\">" +
            "<tr><td style=\"padding:24px;\">" +
            "<p style=\"margin:0 0 4px;color:#718096;font-size:13px;text-transform:uppercase;letter-spacing:0.5px;font-weight:600;\">Order Reference</p>" +
            "<p style=\"margin:0;color:#2d3748;font-size:22px;font-weight:700;\">#" + orderId + "</p>" +
            "</td></tr></table>" +

            // Status badge
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:32px;\">" +
            "<tr><td style=\"background:" + gradient + ";padding:28px;border-radius:12px;text-align:center;\">" +
            "<p style=\"margin:0 0 4px;color:rgba(255,255,255,0.85);font-size:13px;text-transform:uppercase;letter-spacing:1px;font-weight:600;\">Current Status</p>" +
            "<p style=\"margin:0;color:#ffffff;font-size:28px;font-weight:700;letter-spacing:1px;\">" + status + "</p>" +
            "</td></tr></table>" +

            // Thank you note
            "<div style=\"background:#f7fafc;border-left:4px solid " + accentColor + ";padding:16px 20px;border-radius:8px;\">" +
            "<p style=\"color:#4a5568;margin:0;font-size:14px;line-height:1.6;\">" +
            "<strong style=\"color:#2d3748;\">&#128153; Thank you</strong> for shopping with us! " +
            "If you have any questions about your order, don't hesitate to reach out." +
            "</p></div>" +
            "</td></tr>" +

            // Footer
            "<tr><td style=\"background:#f7fafc;padding:32px 40px;text-align:center;border-top:1px solid #e2e8f0;\">" +
            "<p style=\"color:#718096;font-size:14px;margin:0 0 8px;line-height:1.6;\">Need help? Contact us at " +
            "<a href=\"mailto:support@shopping.com\" style=\"color:" + accentColor + ";text-decoration:none;\">support@shopping.com</a></p>" +
            "<p style=\"color:#a0aec0;font-size:12px;margin:0;\">&copy; 2026 Integrated Online Shopping System &middot; TUT Computer Science</p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }

    // -------------------------------------------------------------------------
    // GENERIC
    // -------------------------------------------------------------------------
    private String buildGenericTemplate(EmailRequest r) {
        String subject = r.getSubject() != null ? r.getSubject() : "Notification";
        String body    = r.getBody()    != null ? r.getBody()    : "";

        return "<!DOCTYPE html>" +
            "<html><head>" +
            "<meta charset=\"UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>" +
            "<body style=\"margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;" +
            "background:linear-gradient(135deg,#a8edea 0%,#fed6e3 100%);\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding:40px 20px;\">" +
            "<tr><td align=\"center\">" +
            "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;background:#ffffff;" +
            "border-radius:16px;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.2);\">" +

            // Header
            "<tr><td style=\"background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);padding:40px;text-align:center;\">" +
            "<div style=\"font-size:48px;margin-bottom:16px;\">&#128236;</div>" +
            "<h1 style=\"color:#ffffff;margin:0;font-size:26px;font-weight:700;\">" + subject + "</h1>" +
            "</td></tr>" +

            // Body
            "<tr><td style=\"padding:48px 40px;\">" +
            "<p style=\"color:#4a5568;line-height:1.8;font-size:16px;margin:0;\">" + body + "</p>" +
            "</td></tr>" +

            // Footer
            "<tr><td style=\"background:#f7fafc;padding:32px 40px;text-align:center;border-top:1px solid #e2e8f0;\">" +
            "<p style=\"color:#718096;font-size:14px;margin:0 0 8px;\">" +
            "<a href=\"mailto:support@shopping.com\" style=\"color:#667eea;text-decoration:none;\">support@shopping.com</a></p>" +
            "<p style=\"color:#a0aec0;font-size:12px;margin:0;\">&copy; 2026 Integrated Online Shopping System &middot; TUT Computer Science</p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    // Inserts a Cloudinary fill transformation after the first /image/upload/ segment.
    // Fails safe: anything that is not a Cloudinary upload URL is returned unchanged.
    static String resizeCloudinaryImage(String imageUrl, int width, int height) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return imageUrl;
        }
        String marker = "/image/upload/";
        int markerAt = imageUrl.indexOf(marker);
        if (markerAt < 0) {
            return imageUrl;
        }
        int insertAt = markerAt + marker.length();
        return imageUrl.substring(0, insertAt)
            + "w_" + width + ",h_" + height + ",c_fill/"
            + imageUrl.substring(insertAt);
    }

    // Rand with a thousands separator, e.g. R 1,234.56. Locale-independent on purpose.
    static String formatRand(BigDecimal amount) {
        if (amount == null) {
            return "R 0.00";
        }
        return "R " + String.format(Locale.US, "%,.2f", amount);
    }

    static String formatRand(Double amount) {
        return formatRand(amount != null ? BigDecimal.valueOf(amount) : null);
    }

    // Product names come from the database and land inside markup and attributes.
    static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }
}
