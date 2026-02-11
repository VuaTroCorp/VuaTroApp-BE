package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.dto.SignupRequest;
import fpt.ntu.vuatrovn.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================
    // 1️⃣ SIGNUP
    // =========================
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        authService.signup(request); 
        return ResponseEntity.ok("Signup success");
    }

    // =========================
    // 2️⃣ VERIFY EMAIL
    // =========================
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {

    authService.verifyEmail(token);

    return ResponseEntity.ok("Xác thực email thành công");
    }

    @GetMapping("/login")
    @ResponseBody
    public String login() {
        return "Login page";
}

}
