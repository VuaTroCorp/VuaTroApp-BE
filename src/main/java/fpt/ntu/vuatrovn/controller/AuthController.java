package fpt.ntu.vuatrovn.controller;
import fpt.ntu.vuatrovn.dto.SignupRequest;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.repository.VerificationTokenRepository;
import fpt.ntu.vuatrovn.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthController(VerificationTokenRepository tokenRepository,
                          UserRepository userRepository, AuthService authService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }
    @PostMapping("/signup")
    public User signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token) {

        authService.verifyEmail(token);

        return ResponseEntity.ok("Email verified successfully");
    }
}
