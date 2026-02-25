// package fpt.ntu.vuatrovn.service;

// import fpt.ntu.vuatrovn.dto.*;
// import fpt.ntu.vuatrovn.entity.User;
// import fpt.ntu.vuatrovn.entity.VerificationToken;
// import fpt.ntu.vuatrovn.enums.*;
// import fpt.ntu.vuatrovn.repository.UserRepository;
// import fpt.ntu.vuatrovn.repository.VerificationTokenRepository;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.UUID;

// @Service
// public class AuthService {

//     private final UserRepository userRepository;
//     private final VerificationTokenRepository tokenRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final EmailService emailService;

//     // Constructor Injection
//     public AuthService(UserRepository userRepository,
//                        VerificationTokenRepository tokenRepository,
//                        PasswordEncoder passwordEncoder,
//                        EmailService emailService) {
//         this.userRepository = userRepository;
//         this.tokenRepository = tokenRepository;
//         this.passwordEncoder = passwordEncoder;
//         this.emailService = emailService;
//     }

//     // ===== 1. ĐĂNG KÝ (SIGNUP) =====
//     public User signup(SignupRequest request) {
//         if (userRepository.existsByEmail(request.getEmail())) {
//             throw new RuntimeException("Email đã tồn tại");
//         }

//         if (userRepository.existsByUsername(request.getUsername())) {
//             throw new RuntimeException("Username đã tồn tại");
//         }

//         User user = new User();
//         user.setUsername(request.getUsername());
//         user.setEmail(request.getEmail());
//         user.setPassword(passwordEncoder.encode(request.getPassword()));
        
//         // Cập nhật Enum chuẩn
//         user.setProvider(Provider.LOCAL);
//         user.setProviderId(null);
//         user.setStatus(UserStatus.PENDING); // Đăng ký xong phải chờ kích hoạt

//         User savedUser = userRepository.save(user);

//         // Tạo token xác thực email
//         String token = UUID.randomUUID().toString();
//         VerificationToken vt = new VerificationToken();
//         vt.setToken(token);
//         vt.setUser(savedUser);
//         vt.setExpiryDate(LocalDateTime.now().plusMinutes(15));

//         tokenRepository.save(vt);

//         // Gửi mail
//         emailService.sendVerificationEmail(savedUser.getEmail(), token);

//         return savedUser;
//     }

//     // ===== 2. ĐĂNG NHẬP (LOGIN) - ĐÂY LÀ HÀM BẠN BỊ THIẾU =====
//     public LoginResponse login(LoginRequest request) {
//         // 1. Tìm user theo email
//         User user = userRepository.findByEmail(request.getEmail())
//                 .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không chính xác"));

//         // 2. Kiểm tra mật khẩu
//         if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//             throw new RuntimeException("Mật khẩu không chính xác");
//         }

//         // 3. Kiểm tra trạng thái kích hoạt
//         if (user.getStatus() != UserStatus.ACTIVE) {
//             throw new RuntimeException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email!");
//         }

//         // 4. Tạo LoginResponse
//         // Lưu ý: Do dự án chưa có JWT Utils ở đây, mình trả về token giả tạm thời để Test chạy được.
//         // Sau này bạn cần Inject JwtTokenProvider để sinh token thật nhé.
//         String fakeToken = UUID.randomUUID().toString(); 

//         return new LoginResponse(
//             fakeToken, 
//             user.getRole().name(), 
//             user.getUsername(), 
//             user.getEmail(), 
//             user.getId()
//         );
//     }

//     // ===== 3. XÁC THỰC EMAIL (VERIFY EMAIL) =====
//     public void verifyEmail(String token) {
//         VerificationToken verificationToken = tokenRepository.findByToken(token)
//                 .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc đã hết hạn"));

//         if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//             throw new RuntimeException("Token đã hết hạn");
//         }

//         User user = verificationToken.getUser();
//         user.setStatus(UserStatus.ACTIVE); // Kích hoạt tài khoản thành công
//         userRepository.save(user);

//         tokenRepository.delete(verificationToken); // Xóa token sau khi dùng xong
//     }
// }
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

    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ===== 1. ĐĂNG KÝ (SIGNUP) =====
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // --- SỬA LỖI 1: THÊM ROLE (QUAN TRỌNG) ---
        // Nếu không set Role, user sẽ bị lỗi quyền hạn. Mặc định là USER.
        user.setRole(Role.USER); 
        
        user.setProvider(Provider.LOCAL);
        user.setProviderId(null);
        user.setStatus(UserStatus.PENDING); 

        User savedUser = userRepository.save(user);

        // Tạo token
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(savedUser);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(vt);

        // --- SỬA LỖI 2: TẮT TẠM GỬI MAIL ---
        // emailService.sendVerificationEmail(savedUser.getEmail(), token);
        
        // Thay bằng in ra màn hình để test
        System.out.println("======================================================");
        System.out.println("DEBUG: Đăng ký thành công!");
        System.out.println("DEBUG: Token xác thực của bạn là: " + token);
        System.out.println("DEBUG: Copy token này và chạy API /verify?token=... ");
        System.out.println("======================================================");

        return savedUser;
    }

    // ===== 2. ĐĂNG NHẬP (LOGIN) =====
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra console để lấy token xác thực!");
        }

        String fakeToken = UUID.randomUUID().toString(); 

        return new LoginResponse(
            fakeToken, 
            user.getRole().name(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getId()
        );
    }

    // ===== 3. XÁC THỰC EMAIL (VERIFY EMAIL) =====
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc đã hết hạn"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn");
        }

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE); 
        userRepository.save(user);

        tokenRepository.delete(verificationToken); 
    }
}