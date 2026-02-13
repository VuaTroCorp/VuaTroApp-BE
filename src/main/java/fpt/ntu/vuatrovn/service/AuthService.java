package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.*;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.entity.VerificationToken;
import fpt.ntu.vuatrovn.enums.*;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Constructor Injection (Chuẩn hơn @Autowired)
    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // --- 1. LOGIN (Của bạn - Đã update để dùng logic mới) ---
    public LoginResponse login(LoginRequest request) {
        // Tìm user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Check Provider
        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new RuntimeException("Vui lòng đăng nhập bằng Google");
        }

        // Check Pass
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        // Check Status (Hỗ trợ cả logic cũ và mới)
        if (user.getStatus() == UserStatus.LOCK) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }
        if (user.getStatus() == UserStatus.PENDING) {
            throw new RuntimeException("Vui lòng xác thực email trước khi đăng nhập");
        }

        // Tạo token
        String token = UUID.randomUUID().toString();
        // Xử lý null role cho an toàn
        String roleName = (user.getRole() != null) ? user.getRole().name() : "USER";

        return new LoginResponse(token, roleName, "Đăng nhập thành công");
    }

    // --- 2. SIGNUP (Của nhóm - Đã sửa để dùng Enum) ---
    public User signup(SignupRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Sửa: Dùng Enum thay vì String "local"
        user.setProvider(AuthProvider.LOCAL); 
        user.setProviderId(null);
        user.setStatus(UserStatus.PENDING); 
        user.setRole(UserRole.USER); // Mặc định là USER

        User savedUser = userRepository.save(user);

        // Tạo token xác thực
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(savedUser);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(vt);

        // Gửi email
        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        return savedUser;
    }

    // --- 3. VERIFY EMAIL (Của nhóm) ---
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE); // Kích hoạt tài khoản
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
}