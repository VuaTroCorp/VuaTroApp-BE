package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.dto.*;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API Xác thực (Login/Signup/Verify)")
public class AuthController {

    @Autowired
    private AuthService authService;

    // --- LOGIN (Của bạn) ---
    @Operation(summary = "Đăng nhập hệ thống", description = "Kiểm tra Email/Pass, trả về Token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- SIGNUP (Của nhóm) ---
    @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản mới và gửi email xác thực")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    // --- VERIFY (Của nhóm) ---
    @Operation(summary = "Xác thực Email", description = "API được gọi khi user bấm link trong email")
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}