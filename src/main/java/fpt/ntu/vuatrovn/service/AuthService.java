package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.*;
import fpt.ntu.vuatrovn.entity.PasswordReset;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.entity.VerificationToken;
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.repository.PasswordResetRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.repository.VerificationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {
    // 🔥 ĐÃ BỔ SUNG DÒNG NÀY - Đây là "chìa khóa" để hết lỗi
    private final UserRepository userRepository; 
    
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetRepository passwordResetRepository;

    // Constructor Injection
    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       PasswordResetRepository passwordResetRepository) {
        this.userRepository = userRepository; // Bây giờ gán mới không bị lỗi nữa
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordResetRepository = passwordResetRepository;
    }

    // =========================
    // 1. ĐĂNG KÝ (SIGNUP)
    // =========================
    public User signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username đã tồn tại");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không khớp");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider(Provider.LOCAL);
        user.setStatus(UserStatus.PENDING);
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        // Tạo token xác thực
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(savedUser);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(vt);

        // Gửi mail (Nhớ kiểm tra mail trap hoặc cấu hình SMTP nhé)
        emailService.sendVerificationEmail(savedUser.getEmail(), token);
        return savedUser;
    }

    // =========================
    // 2. ĐĂNG NHẬP (LOGIN)
    // =========================
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email!");
        }

        String fakeToken = UUID.randomUUID().toString();
        
        return new LoginResponse(200, fakeToken, user.getRole().name(), "Đăng nhập thành công");
    }

    // =========================
    // 3. XÁC THỰC EMAIL (VERIFY)
    // =========================
    public void verifyEmail(String token) {
        VerificationToken vt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token không hợp lệ"));

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(vt); 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token đã hết hạn");
        }

        User user = vt.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        tokenRepository.delete(vt);
    }

    // 4.Check Email và tạo Otp ( FORGOT PASSWORD)
    public String generatePasswordOtpCode(String email){

        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email không tồn tại")
        );

//        Có thì tạo mã otp
//        Ramdom mã otp 6 chữ số
        String optCode = String.format(
                "%6d",new SecureRandom().nextInt(1_000_000)
        );

        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setOtp(optCode);
        passwordReset.setOtp_expiry(
                Instant.now().plus(5, ChronoUnit.MINUTES)
        );
        passwordReset.setUser(user);

        passwordResetRepository.save(passwordReset);
        return "Đã gửi mã otp vui lòng check email/database";
    }

    //4.1 CheckOpt và tạo resetToken;
    public String verifyOtpCode(String optCode){

        Instant now = Instant.now();
        // Verify otp
        PasswordReset passwordReset = this.passwordResetRepository.findByOtp(optCode).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Sai mã OTP")
        );

        //Check otp xem còn hạn không
        if (now.isAfter(passwordReset.getOtp_expiry())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Otp hết hạn. Vui lòng thực hiện lại");
        }
        //Tạo reset Token;
        String resetToken = UUID.randomUUID().toString();

        passwordReset.setResetToken(resetToken);
        passwordReset.setToken_expiry(
                Instant.now().plus(5,ChronoUnit.MINUTES)
        );
        passwordReset.setVerified(true);
        passwordReset.setUsed(true);
        this.passwordResetRepository.save(passwordReset);

        return resetToken;
    }

    //4.2 Check Reset Token và thay đổi mật khẩu;
    public String changePassword(ChangePasswordRequest request){
        //Check reset Token
        PasswordReset passwordReset = this.passwordResetRepository.findByResetToken(request.getResetToken()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"reset token bị sai")
        );
        // kiểm tra token còn hạn không
        Instant now = Instant.now();
        if (now.isAfter(passwordReset.getToken_expiry())){
            passwordResetRepository.delete(passwordReset);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Reset token hết hạn vui lòng thực hiện lại");
        }
        // Thực hiện đổi mật khẩu
        User user = this.userRepository.findById(
                passwordReset.getUser().getId()
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"không tìm thấy email"));

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        passwordResetRepository.delete(passwordReset);

        return "Đổi mật khẩu thành công";
    }

}