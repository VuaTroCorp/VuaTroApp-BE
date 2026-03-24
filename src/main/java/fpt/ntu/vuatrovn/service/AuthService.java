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
import fpt.ntu.vuatrovn.service.email.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository; 
    
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetRepository passwordResetRepository;
    private final JwtService jwtService;

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

        // Gửi mail
        emailService.sendVerificationEmail(savedUser.getEmail(), token);
        return savedUser;
    }

    // =========================
    // 2. ĐĂNG NHẬP (LOGIN)
    // =========================
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Email hoặc mật khẩu không chính xác"
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email hoặc mật khẩu không chính xác"
            );
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email!"
            );
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name()
        );
        return new LoginResponse(
                200,
                token,
                user.getRole().name(),
                "Đăng nhập thành công"
        );
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
    public String generateAndSendPasswordResetOtp(ForgotPasswordRequest request){

        User user = this.userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email không tồn tại")
        );

//        Kiểm tra xem có reset token chưa
        Optional<PasswordReset> optionalReset = this.passwordResetRepository.findByUser_Email(request.getEmail());

        //Ramdom reset token chữ số
        String resetToken = UUID.randomUUID().toString();

        if (optionalReset.isPresent()){
            PasswordReset reset = optionalReset.get();
            /*Check otp chua het han*/
            if (Instant.now().isBefore(reset.getToken_expiry())){
                return "OTP đã được gửi. Vui lòng kiểm tra email hoặc thử lại sau.";
            }
            passwordResetRepository.delete(reset);
        }
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setResetToken(resetToken);
        passwordReset.setToken_expiry(
                Instant.now().plus(1,ChronoUnit.MINUTES)
        );
        passwordReset.setUser(user);
        passwordResetRepository.save(passwordReset);
        emailService.sendOtpEmail(user.getEmail(),resetToken);
        return "Vui lòng kiểm tra mail";
    }

    //4.1 Xác thực qua email
    public String verifyPasswordResetOtpEmail(String resetToken){
        PasswordReset passwordReset = this.passwordResetRepository.findByResetToken(resetToken).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Reset token không tìm thấy")
        );

//        Check token còn hạn hay ko
        if (Instant.now().isAfter(passwordReset.getToken_expiry())){
            this.passwordResetRepository.delete(passwordReset);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Token hết hạn vui lòng thực hiện lại");
        }
        passwordReset.setVerified(true);

        passwordResetRepository.save(passwordReset);

        return resetToken;
    }

    //4.3 Check Reset Token và thay đổi mật khẩu;
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

        //Kiểm tra xem verify có là true chưa
        if (!passwordReset.isVerified()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Token chưa xác nhận vui lòng kiểm tra mail");
        }
        // Thực hiện đổi mật khẩu
        User user = this.userRepository.findById(
                passwordReset.getUser().getId()
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"không tìm thấy email"));

        String passwordHash = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(passwordHash);
        userRepository.save(user);
        passwordResetRepository.delete(passwordReset);

        return "Đổi mật khẩu thành công";
    }


}