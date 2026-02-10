package fpt.ntu.vuatrovn.service;

import java.time.LocalDateTime;
import java.util.UUID;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.entity.UserStatus;
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
    public void verifyEmail(String token) {

        VerificationToken verificationToken =
                tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        tokenRepository.delete(verificationToken); // optional nhưng rất nên
    }
    public User signup(SignupRequest request) {

         User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("local");
        user.setProviderId(null);
    user.setStatus(UserStatus.PENDING); // 🔴 chưa verify


        // 1️⃣ Lưu user trước
        User savedUser = userRepository.save(user);

        // 2️⃣ Tạo token
        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(savedUser);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(vt);

        // 3️⃣ Gửi email
        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        // 4️⃣ Return
        return savedUser;
    }
}
