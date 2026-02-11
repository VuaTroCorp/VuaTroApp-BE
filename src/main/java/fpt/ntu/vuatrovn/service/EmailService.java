package fpt.ntu.vuatrovn.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendVerificationEmail(String email, String token) {
        // DEMO: log ra console
        System.out.println("VERIFY EMAIL:");
        System.out.println("Email: " + email);
        System.out.println("Link: http://localhost:8080/api/auth/verify?token=" + token);
    }
}