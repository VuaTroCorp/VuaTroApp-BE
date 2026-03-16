package fpt.ntu.vuatrovn.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String token) {

        String link =
            "http://localhost:8080/api/auth/verify?token=" + token;

        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                
                <h2 style="color: #2E86C1;">
                    Xác thực tài khoản VuaTroVN
                </h2>

                <p>Xin chào,</p>

                <p>
                    Cảm ơn bạn đã đăng ký tài khoản tại 
                    <b>VuaTroVN</b>.
                </p>

                <p>
                    Vui lòng bấm nút bên dưới để xác thực email:
                </p>

                <a href="%s"
                   style="
                        display: inline-block;
                        padding: 12px 24px;
                        background-color: #28a745;
                        color: white;
                        text-decoration: none;
                        border-radius: 6px;
                        font-weight: bold;
                   ">
                    XÁC THỰC NGAY
                </a>

                <p style="margin-top: 20px;">
                    Hoặc copy link này:
                </p>

                <p>
                    <a href="%s">%s</a>
                </p>

                <hr>

                <p style="font-size: 12px; color: gray;">
                    Link có hiệu lực trong 15 phút.
                </p>

                <p style="font-size: 12px; color: gray;">
                    Nếu bạn không đăng ký, hãy bỏ qua email này.
                </p>

            </div>
        """.formatted(link, link, link);

        try {
            MimeMessage message =
                mailSender.createMimeMessage();

            MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Xác thực tài khoản VuaTroVN");
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);

            System.out.println(
                "Sent verify mail to: " + email
            );

        } catch (MessagingException e) {
            throw new RuntimeException(
                "Gửi email thất bại", e
            );
        }
    }

    public void confirmEmailChangeOtp(String email, String otp) {

        String link =
                "http://localhost:8080/api/user/verify-otp?email=" + email + "&otp=" + otp;

        String htmlContent = """
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            
            <h2 style="color: #2E86C1;">
                Xác thực OTP - VuaTroVN
            </h2>

            <p>Xin chào,</p>

            <p>
                Mã OTP của bạn là:
            </p>

            <h1 style="color:#e74c3c;">%s</h1>

            <p>
                Hoặc bấm nút bên dưới để xác thực:
            </p>

            <a href="%s"
               style="
                    display: inline-block;
                    padding: 12px 24px;
                    background-color: #28a745;
                    color: white;
                    text-decoration: none;
                    border-radius: 6px;
                    font-weight: bold;
               ">
                XÁC THỰC OTP
            </a>

            <p style="margin-top:20px;">
                Hoặc copy link:
            </p>

            <p>
                <a href="%s">%s</a>
            </p>

            <hr>

            <p style="font-size: 12px; color: gray;">
                OTP có hiệu lực trong 15 phút.
            </p>

        </div>
    """.formatted(otp, link, link, link);

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("OTP xác thực - VuaTroVN");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException("Gửi email OTP thất bại", e);

        }
    }
}
