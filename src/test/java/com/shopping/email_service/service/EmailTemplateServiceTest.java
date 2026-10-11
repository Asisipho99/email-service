package com.shopping.email_service.service;

import com.shopping.email_service.dto.EmailRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EmailTemplateServiceTest {

    private static final Pattern IMG_TAG = Pattern.compile("<img[^>]*>");

    private final EmailTemplateService templateService = new EmailTemplateService();

    // -------------------------------------------------------------------------
    // ITEM TABLE
    // -------------------------------------------------------------------------

    @Test
    void multiItemOrderRendersEveryProductNameAndAbsoluteHttpsImage() {
        EmailRequest request = orderRequest(List.of(
            line("Rooibos Road Cold Pressed Ground Coffee", 2, "385.66",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg"),
            line("Playgrove Collaborative Remote Control Car", 1, "802.13",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/qgg88bgokojs7thdcmwz.jpg"),
            line("Utility Works Refillable Reusable Bottle", 3, "1114.47",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/ckkghqx3zskowccp2nvu.jpg")
        ));

        String html = templateService.buildTemplate(request);

        assertThat(html)
            .contains("Rooibos Road Cold Pressed Ground Coffee")
            .contains("Playgrove Collaborative Remote Control Car")
            .contains("Utility Works Refillable Reusable Bottle");

        List<String> imgTags = imgTags(html);
        assertThat(imgTags).hasSize(3);
        assertThat(imgTags).allSatisfy(tag ->
            assertThat(srcOf(tag))
                .startsWith("https://res.cloudinary.com")
                .contains("w_80,h_80,c_fill"));
    }

    @Test
    void everyImgHasNonEmptyAltAndExplicitWidthAndHeight() {
        EmailRequest request = orderRequest(List.of(
            line("Rooibos Road Cold Pressed Ground Coffee", 2, "385.66",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg"),
            line("Utility Works Refillable Reusable Bottle", 3, "1114.47",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/ckkghqx3zskowccp2nvu.jpg")
        ));

        List<String> imgTags = imgTags(templateService.buildTemplate(request));

        assertThat(imgTags).hasSize(2);
        assertThat(imgTags).allSatisfy(tag -> {
            assertThat(attributeOf(tag, "alt")).isNotBlank();
            assertThat(tag).contains("width=\"80\"").contains("height=\"80\"");
        });
    }

    @Test
    void nullImageUrlItemStillRendersNameAndEmitsNoImgTag() {
        EmailRequest request = orderRequest(List.of(
            line("Rooibos Road Cold Pressed Ground Coffee", 2, "385.66",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg"),
            line("Playgrove Collaborative Remote Control Car", 1, "802.13", null)
        ));

        String html = templateService.buildTemplate(request);

        assertThat(html)
            .contains("Rooibos Road Cold Pressed Ground Coffee")
            .contains("Playgrove Collaborative Remote Control Car")
            .doesNotContain("src=\"\"");
        assertThat(imgTags(html)).hasSize(1);
    }

    @Test
    void nullItemListFallsBackToProductListWithoutThrowing() {
        EmailRequest request = orderRequest(null);

        assertThatCode(() -> templateService.buildTemplate(request)).doesNotThrowAnyException();

        String html = templateService.buildTemplate(request);
        assertThat(html)
            .contains("2 x Coffee, 1 x Remote Control Car")
            .contains("Order Confirmed!")
            .doesNotContain("<img");
    }

    @Test
    void emptyItemListFallsBackToProductList() {
        String html = templateService.buildTemplate(orderRequest(List.of()));

        assertThat(html)
            .contains("2 x Coffee, 1 x Remote Control Car")
            .contains("Order Confirmed!")
            .doesNotContain("<img");
    }

    @Test
    void escapesHtmlInProductName() {
        EmailRequest request = orderRequest(List.of(
            line("Tea & <script>alert(1)</script>", 1, "99.99",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg")
        ));

        String html = templateService.buildTemplate(request);

        assertThat(html)
            .contains("Tea &amp; &lt;script&gt;alert(1)&lt;/script&gt;")
            .doesNotContain("<script");
    }

    @Test
    void registrationStatusUpdateAndGenericTemplatesIgnoreItems() {
        List<EmailRequest.OrderItemLine> items = List.of(
            line("Rooibos Road Cold Pressed Ground Coffee", 2, "385.66",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg")
        );

        EmailRequest registration = orderRequest(items);
        registration.setType("REGISTRATION");
        String registrationHtml = templateService.buildTemplate(registration);
        assertThat(registrationHtml).contains("Welcome Aboard!").doesNotContain("<img");

        EmailRequest statusUpdate = orderRequest(items);
        statusUpdate.setType("STATUS_UPDATE");
        statusUpdate.setOrderStatus("SHIPPED");
        String statusHtml = templateService.buildTemplate(statusUpdate);
        assertThat(statusHtml).contains("Order Update").doesNotContain("<img");

        EmailRequest generic = orderRequest(items);
        generic.setType("SOMETHING_ELSE");
        String genericHtml = templateService.buildTemplate(generic);
        assertThat(genericHtml).contains("Order Confirmation").doesNotContain("<img");
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    @Test
    void resizeCloudinaryImageRewritesWellFormedUrl() {
        String original = "https://res.cloudinary.com/dumw3ubjh/image/upload/"
            + "v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg";

        assertThat(EmailTemplateService.resizeCloudinaryImage(original, 80, 80)).isEqualTo(
            "https://res.cloudinary.com/dumw3ubjh/image/upload/w_80,h_80,c_fill/"
                + "v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg");
    }

    @Test
    void resizeCloudinaryImageLeavesNonMatchingUrlUntouched() {
        assertThat(EmailTemplateService.resizeCloudinaryImage("https://example.com/a/photo.jpg", 80, 80))
            .isEqualTo("https://example.com/a/photo.jpg");
        assertThat(EmailTemplateService.resizeCloudinaryImage(null, 80, 80)).isNull();
        assertThat(EmailTemplateService.resizeCloudinaryImage("   ", 80, 80)).isEqualTo("   ");
    }

    @Test
    void formatRandUsesSpacedRandAndThousandsSeparator() {
        assertThat(EmailTemplateService.formatRand(new BigDecimal("1234.56"))).isEqualTo("R 1,234.56");
        assertThat(EmailTemplateService.formatRand((BigDecimal) null)).isEqualTo("R 0.00");
        assertThat(EmailTemplateService.formatRand(4916.86)).isEqualTo("R 4,916.86");
        assertThat(EmailTemplateService.formatRand((Double) null)).isEqualTo("R 0.00");
    }

    // -------------------------------------------------------------------------
    // SAMPLE RENDER
    // -------------------------------------------------------------------------

    @Test
    void writesSampleOrderEmailHtmlForManualInspection() throws IOException {
        EmailRequest request = orderRequest(List.of(
            line("Rooibos Road Cold Pressed Ground Coffee", 2, "385.66",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/rzokai6poputkmxaydxu.jpg"),
            line("Playgrove Collaborative Remote Control Car", 1, "802.13",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/qgg88bgokojs7thdcmwz.jpg"),
            line("Utility Works Refillable Reusable Bottle", 3, "1114.47",
                "https://res.cloudinary.com/dumw3ubjh/image/upload/v1791716616/shopping-products/ckkghqx3zskowccp2nvu.jpg")
        ));

        Path sample = Path.of("target", "sample-order-email.html");
        Files.createDirectories(sample.getParent());
        Files.writeString(sample, templateService.buildTemplate(request));

        assertThat(sample).exists();
        assertThat(Files.size(sample)).isPositive();
    }

    // -------------------------------------------------------------------------
    // FIXTURES
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // DEGRADED ITEM DATA
    // -------------------------------------------------------------------------

    @Test
    void nonHttpsImageUrlIsNotRendered() {
        // resizeCloudinaryImage returns anything it does not recognise
        // unchanged, so without an explicit guard a relative or http:// value
        // would reach the email as a broken image. A product seeded straight
        // into the database rather than through the Cloudinary upload path can
        // carry exactly that.
        for (String hostile : List.of("/assets/no-image.png",
                                      "http://insecure.example.com/x.jpg",
                                      "assets/no-image.png")) {
            EmailRequest.OrderItemLine item =
                line("Kudu Adjustable Resistance Bands", 1, "149.00", hostile);
            String html = templateService.buildTemplate(orderRequest(List.of(item)));

            assertThat(imgTags(html))
                .as("no <img> for src %s", hostile)
                .isEmpty();
            assertThat(html)
                .as("name still shown as text for src %s", hostile)
                .contains("Kudu Adjustable Resistance Bands");
        }
    }

    @Test
    void nullQuantityDoesNotClaimAQuantityOfOne() {
        EmailRequest.OrderItemLine item = new EmailRequest.OrderItemLine();
        item.setProductName("Stoneford Handmade Watering Can");
        item.setQuantity(null);
        item.setUnitPrice(new BigDecimal("249.00"));

        String html = templateService.buildTemplate(orderRequest(List.of(item)));

        assertThat(html).contains("Stoneford Handmade Watering Can");
        assertThat(html)
            .as("must not assert a quantity the order never supplied")
            .doesNotContain("Qty 1");
        assertThat(html).contains("each");
    }

    @Test
    void nullQuantityAndNullPriceStillRendersTheName() {
        EmailRequest.OrderItemLine item = new EmailRequest.OrderItemLine();
        item.setProductName("Velodrome All Weather Yoga Mat");

        assertThatCode(() ->
            templateService.buildTemplate(orderRequest(List.of(item))))
            .doesNotThrowAnyException();

        String html = templateService.buildTemplate(orderRequest(List.of(item)));
        assertThat(html).contains("Velodrome All Weather Yoga Mat");
        assertThat(html).doesNotContain("Qty 1");
    }

    private static EmailRequest orderRequest(List<EmailRequest.OrderItemLine> items) {
        EmailRequest request = new EmailRequest();
        request.setTo("thandi.mokoena@example.com");
        request.setSubject("Order Confirmation");
        request.setBody("Order #1042 total R 4,916.86");
        request.setType("ORDER_CONFIRMATION");
        request.setCustomerName("Thandi Mokoena");
        request.setOrderId("1042");
        request.setProductList("2 x Coffee, 1 x Remote Control Car");
        request.setTotalAmount(4916.86);
        request.setItems(items);
        return request;
    }

    private static EmailRequest.OrderItemLine line(String productName, int quantity,
                                                   String unitPrice, String imageUrl) {
        BigDecimal price = new BigDecimal(unitPrice);
        EmailRequest.OrderItemLine item = new EmailRequest.OrderItemLine();
        item.setProductName(productName);
        item.setQuantity(quantity);
        item.setUnitPrice(price);
        item.setLineTotal(price.multiply(BigDecimal.valueOf(quantity)));
        item.setImageUrl(imageUrl);
        return item;
    }

    private static List<String> imgTags(String html) {
        List<String> tags = new ArrayList<>();
        Matcher matcher = IMG_TAG.matcher(html);
        while (matcher.find()) {
            tags.add(matcher.group());
        }
        return tags;
    }

    private static String srcOf(String imgTag) {
        return attributeOf(imgTag, "src");
    }

    private static String attributeOf(String imgTag, String attribute) {
        Matcher matcher = Pattern.compile(attribute + "=\"([^\"]*)\"").matcher(imgTag);
        return matcher.find() ? matcher.group(1) : null;
    }
}
