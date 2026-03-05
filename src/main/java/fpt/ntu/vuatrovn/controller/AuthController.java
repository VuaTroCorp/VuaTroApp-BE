package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.dto.*;
import fpt.ntu.vuatrovn.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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

    // 3️⃣ FORGOT PASSWORD (QUÊN MẬT KHẨU)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody ForgotPasswordRequest request){
//        Check email và tạo otp
        String content = this.authService.generatePasswordOtpCode(request);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/verify-otp-mail")
    public ResponseEntity<?> verifyOtpCode(@RequestParam String otp){

        String resetToken = this.authService.verifyPasswordResetOtpEmail(otp);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Xác thực otp thành công");
        response.put("resetToken",resetToken);

        return ResponseEntity.ok(response);
    }

    // 3️⃣.1 VERIFY OTP CODE (QUÊN MẬT KHẨU)
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtpCode(@RequestBody VerifyOtpRequest request)
    {
//        Check mã otp;
        String resetToken = this.authService.verifyOtpCode(request);
        return ResponseEntity.ok(Map.of("resetToken",resetToken));
    }

    // 3️⃣.2 Change Password (QUÊN MẬT KHẨU)
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