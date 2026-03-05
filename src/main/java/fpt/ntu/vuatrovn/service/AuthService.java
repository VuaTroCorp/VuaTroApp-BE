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
import java.util.Optional;
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
    public String generatePasswordOtpCode(ForgotPasswordRequest request){

        User user = this.userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email không tồn tại")
        );

//        Kiểm tra xem có otp chưa
        Optional<PasswordReset> optionalReset = this.passwordResetRepository.findByUser_Email(request.getEmail());

        //        Ramdom mã otp 6 chữ số
        String optCode = String.format(
                "%06d",new SecureRandom().nextInt(1_000_000)
        );

        if (optionalReset.isEmpty()){
           // Có thì tạo mã otp

            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setOtp(optCode);
            passwordReset.setOtp_expiry(
                    Instant.now().plus(5, ChronoUnit.MINUTES)
            );
            passwordReset.setUser(user);

            this.passwordResetRepository.save(passwordReset);
            //Gửi mail
            this.emailService.sendOtpEmail(user.getEmail(),optCode);
            return optCode;
        } else if (Instant.now().isAfter(optionalReset.get().getOtp_expiry())){
            this.passwordResetRepository.delete(optionalReset.get());
//            Tạo bản ghi mới
            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setOtp(optCode);
            passwordReset.setOtp_expiry(
                    Instant.now().plus(5, ChronoUnit.MINUTES)
            );
            passwordReset.setUser(user);
            this.passwordResetRepository.save(passwordReset);
            //Gửi lại otp
            this.emailService.sendOtpEmail(user.getEmail(),optCode);
            return optCode;
        }

        return optionalReset.get().getOtp();
    }

    //4.1 Xác thực qua email
    public String verifyPasswordResetOtpEmail(String otpCode){
        PasswordReset passwordReset = this.passwordResetRepository.findByOtp(otpCode);
        //Tạo reset Token;
        String resetToken = UUID.randomUUID().toString();

        passwordReset.setResetToken(resetToken);
        passwordReset.setToken_expiry(Instant.now().plus(5,ChronoUnit.MINUTES));
        passwordReset.setVerified(true);
        passwordReset.setUsed(true);

        passwordResetRepository.save(passwordReset);

        return resetToken;
    }



    //4.2 CheckOpt và tạo resetToken --> nhập sai 5 lần khóa chức năng đổi password cho đến khi otp hết hạn;
    public String verifyOtpCode(VerifyOtpRequest request){

        Instant now = Instant.now();

        //Check email tồn tại
        PasswordReset passwordReset = this.passwordResetRepository.findByUser_Email(request.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"email không đúng")
        );

        //Check otp xem còn hạn không
        if (now.isAfter(passwordReset.getOtp_expiry())){
            //Hết hạn thì xóa token cũ đi
            this.passwordResetRepository.delete(passwordReset);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Otp hết hạn. Vui lòng thực hiện lại");
        }

        if (passwordReset.isBlock()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User đang bị block chức năng quên mật khẩu vui lòng thử lại sau 5'");
        }


        //Check mã otp
        if (!passwordReset.getOtp().equals(request.getOtpCode())){
           passwordReset.setCountTryOtp(
                   passwordReset.getCountTryOtp() + 1
           );
           if (passwordReset.getCountTryOtp() >= 5){
               passwordReset.setBlock(true);
           }
           passwordResetRepository.save(passwordReset);
           int countTry = 5 - passwordReset.getCountTryOtp();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sai mã OTP. Bạn còn " + countTry + " lần thử");
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