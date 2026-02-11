package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.dto.*;
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
@Tag(name = "Authentication", description = "API quản lý xác thực người dùng") // [Swagger] Tên nhóm API
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Đăng nhập hệ thống", description = "Kiểm tra Email/Pass, trả về Token nếu hợp lệ") // [Swagger] Mô tả API
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đăng nhập thành công, trả về Token"),
        @ApiResponse(responseCode = "400", description = "Sai thông tin hoặc tài khoản bị khóa")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Trả về lỗi 400 kèm thông báo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}