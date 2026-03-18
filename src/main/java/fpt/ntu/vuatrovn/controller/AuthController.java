package fpt.ntu.vuatrovn.controller;

import java.util.HashMap;
import java.util.Map;

import fpt.ntu.vuatrovn.dto.UserResponse;
import fpt.ntu.vuatrovn.service.user.UserServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.ntu.vuatrovn.dto.LoginRequest;
import fpt.ntu.vuatrovn.dto.LoginResponse;
import fpt.ntu.vuatrovn.dto.SignupRequest;
import fpt.ntu.vuatrovn.service.AuthService;
import fpt.ntu.vuatrovn.dto.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    private final UserServiceImp userServiceImp;
    public AuthController(AuthService authService, UserServiceImp userServiceImp) {
        this.authService = authService;
        this.userServiceImp = userServiceImp;
    }

    // =========================
    // 1️⃣ SIGNUP (ĐĂNG KÝ)
    // =========================
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        authService.signup(request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Đăng ký thành công. Vui lòng kiểm tra email để xác thực.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // 2️⃣ VERIFY EMAIL (XÁC THỰC)
    // =========================
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        authService.verifyEmail(token);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Xác thực email thành công");

        return ResponseEntity.ok(response);
    }

    // =========================
    // 3️⃣ LOGIN (ĐĂNG NHẬP) - PHẦN VỪA ĐƯỢC BỔ SUNG
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Gọi service để lấy thông tin người dùng và gán lại cho dto

        UserResponse response = this.userServiceImp.getUserInfo(authentication.getName());
        return ResponseEntity.ok(response);
    }
    // 4️⃣ FORGOT PASSWORD (QUÊN MẬT KHẨU)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody ForgotPasswordRequest request){
//        Check email và tạo otp
        String content = this.authService.generateAndSendPasswordResetOtp(request);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/verify-resettoken-mail")
    public ResponseEntity<?> verifyResetPasswordEmail(@RequestParam String resetToken){

        String verifyResetPasswordEmail = this.authService.verifyPasswordResetOtpEmail(resetToken);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Xác thực thành công");
        response.put("resetToken",verifyResetPasswordEmail);

        return ResponseEntity.ok(response);
    }

    // 4️⃣.2 Change Password (QUÊN MẬT KHẨU)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request){
       if (!request.getComfirmPassword().equals(request.getNewPassword())){
           return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp");
       }

//       Check reset token;
        String changePassword = this.authService.changePassword(request);

        return ResponseEntity.ok(changePassword);
    }
}