package fpt.ntu.vuatrovn.service;

<<<<<<< HEAD
import fpt.ntu.vuatrovn.dto.*;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.*;
import fpt.ntu.vuatrovn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
=======
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
>>>>>>> 32137ab80f1d68d69ef445ca829c9f574ef91899

@Service
public class AuthService {

<<<<<<< HEAD
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // 2. Kiểm tra Provider (Chỉ LOCAL mới được login bằng pass)
        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new RuntimeException("Vui lòng đăng nhập bằng Google");
        }

        // 3. Kiểm tra mật khẩu (So sánh pass nhập vào vs pass trong DB)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHashed())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        // 4. Kiểm tra trạng thái
        if (user.getStatus() == UserStatus.LOCK) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // 5. Sinh Token giả lập (Thực tế sẽ dùng JWT)
        String token = UUID.randomUUID().toString();

        return new LoginResponse(token, user.getRole().name(), "Đăng nhập thành công");
    }
}
=======
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
>>>>>>> 32137ab80f1d68d69ef445ca829c9f574ef91899
