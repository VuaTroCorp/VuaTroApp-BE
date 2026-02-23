package fpt.ntu.vuatrovn.service;

import java.time.LocalDateTime;
import java.util.UUID;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.entity.VerificationToken;
import fpt.ntu.vuatrovn.dto.SignupRequest;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ===== VERIFY EMAIL =====
    public void verifyEmail(String token) {

        VerificationToken vt =
            tokenRepository.findByToken(token)
            .orElseThrow(() ->
                new RuntimeException("Token không hợp lệ"));

        if (vt.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Token đã hết hạn");
        }

        User user = vt.getUser();
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        tokenRepository.delete(vt);
    }

    // ===== SIGNUP =====
    public User signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        user.setProvider(Provider.LOCAL);
        user.setProviderId(null);
        user.setStatus(UserStatus.PENDING);

        User savedUser = userRepository.save(user);

        // tạo token
        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(savedUser);
        vt.setExpiryDate(
            LocalDateTime.now().plusMinutes(15)
        );

        tokenRepository.save(vt);

        // gửi mail
        emailService.sendVerificationEmail(
            savedUser.getEmail(),
            token
        );

        return savedUser;
    }
}
