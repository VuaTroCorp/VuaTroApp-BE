package fpt.ntu.vuatrovn.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

        private final JavaMailSender mailSender;
            public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendVerificationEmail(String email, String token) {
        // DEMO: log ra console
        System.out.println("VERIFY EMAIL:");
        System.out.println("Email: " + email);
        System.out.println("Link: http://localhost:8080/api/auth/verify?token=" + token);
    }
        public void sendEmail(String to, String subject, String content) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
    public void sendResetPasswordEmail(String toEmail, String token) {

    String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;

    String subject = "Reset your password";

    String content = """
            Hello,

            You requested to reset your password.

            Click the link below to reset:
            """ + resetLink + """

            This link will expire in 15 minutes.
            """;

    sendEmail(toEmail, subject, content);
}

}