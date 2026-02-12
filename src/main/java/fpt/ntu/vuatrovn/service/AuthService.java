package fpt.ntu.vuatrovn.service;

import java.time.LocalDateTime;
import java.util.UUID;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.entity.UserStatus;
import fpt.ntu.vuatrovn.entity.VerificationToken;
import fpt.ntu.vuatrovn.entity.PasswordResetToken;
import fpt.ntu.vuatrovn.dto.SignupRequest;
import fpt.ntu.vuatrovn.repository.PasswordResetTokenRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.repository.VerificationTokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {

        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // =============================
    // VERIFY EMAIL
    // =============================
    public void verifyEmail(String token) {

        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    // =============================
    // SIGNUP
    // =============================
    public User signup(SignupRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("local");
        user.setProviderId(null);
        user.setStatus(UserStatus.PENDING);

        User savedUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(savedUser);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        verificationTokenRepository.save(vt);

        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        return savedUser;
    }

    // =============================
    // FORGOT PASSWORD
    // =============================
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);

        emailService.sendResetPasswordEmail(email, token);
    }

    // =============================
    // RESET PASSWORD
    // =============================
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }
}
