package fpt.ntu.vuatrovn.controller;

import java.util.HashMap;
import java.util.Map;

import fpt.ntu.vuatrovn.dto.UserResponse;
import fpt.ntu.vuatrovn.service.UserService;
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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;
    public AuthController(AuthService authService,UserService userService) {
        this.authService = authService;
        this.userService = userService;
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

        UserResponse response = this.userService.getUserInfo(authentication.getName());
        return ResponseEntity.ok(response);
    }
}