package fpt.ntu.vuatrovn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.ntu.vuatrovn.dto.LoginRequest;  // Mới thêm
import fpt.ntu.vuatrovn.dto.LoginResponse; // Mới thêm
import fpt.ntu.vuatrovn.dto.SignupRequest;
import fpt.ntu.vuatrovn.service.AuthService;

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
        return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt.");
    }

    // =========================
    // 2️⃣ VERIFY EMAIL (XÁC THỰC)
    // =========================
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Xác thực email thành công! Bạn có thể đăng nhập ngay bây giờ.");
    }

    // =========================
    // 3️⃣ LOGIN (ĐĂNG NHẬP) - MỚI THÊM
    // =========================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Gọi hàm login bên Service mà chúng ta vừa viết lúc nãy
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}