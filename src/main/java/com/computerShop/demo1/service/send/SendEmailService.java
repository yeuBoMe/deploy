package com.computerShop.demo1.service.send;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendEmailService {

    private final SendGrid sendGrid;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    public SendEmailService(@Value("${sendgrid.api.key}") String sendGridApiKey) {
        this.sendGrid = new SendGrid(sendGridApiKey);
    }

    public void sendOrderConfirmationEmail(String toEmail, String receiverName, String receiverAddress, String receiverPhone, String orderDetails, double totalPrice) throws IOException {
        Email from = new Email(fromEmail);  // Email gửi từ SendGrid
        Email to = new Email(toEmail);  // Email người nhận
        String subject = "Cảm ơn bạn đã đặt hàng [Đơn hàng #" + System.currentTimeMillis() + "]";

        // Nội dung email dạng HTML
        String htmlContent = """
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='background-color: #6f42c1; padding: 20px; color: white; text-align: center;'>
                    <h1>Cảm ơn bạn đã đặt hàng</h1>
                </div>
                <div style='padding: 20px;'>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Cảm ơn bạn đã đặt hàng. Đơn hàng của bạn đang được giữ cho đến khi chúng tôi xác nhận đã nhận được thanh toán.</p>
                    <p><strong>[Đơn hàng #%d] (%s)</strong></p>
                    <table border='1' style='width: 100%%; border-collapse: collapse; margin: 20px 0;'>
                        <thead>
                            <tr style='background-color: #f8f9fa;'>
                                <th style='padding: 10px;'>Sản phẩm</th>
                                <th style='padding: 10px;'>Số lượng</th>
                                <th style='padding: 10px;'>Giá</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>
                    <p><strong>Tổng số phụ:</strong> %,.0f đ</p>
                    <p><strong>Phương thức thanh toán:</strong> Thanh toán khi nhận hàng</p>
                    <p><strong>Tổng số tiền:</strong> %,.0f đ</p>
                    <h3>Địa chỉ thanh toán</h3>
                    <p>%s</p>
                    <p>%s</p>
                    <p>%s</p>
                    <p>Chúng tôi đang tiếp nhận thông tin đơn đặt hàng của bạn.</p>
                </div>
            </body>
            </html>
            """.formatted(
                receiverName,
                System.currentTimeMillis(),
                java.time.LocalDate.now().toString(),
                orderDetails,
                totalPrice,
                totalPrice,
                receiverName,
                receiverAddress,
                receiverPhone
            );

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            System.out.println("SendGrid response status: " + response.getStatusCode());
            System.out.println("SendGrid response body: " + response.getBody());
        } catch (IOException ex) {
            System.err.println("Failed to send email via SendGrid: " + ex.getMessage());
            throw ex;
        }
    }
}