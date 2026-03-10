package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.dto.UserRequest;
import fpt.ntu.vuatrovn.service.SmsService;
import fpt.ntu.vuatrovn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SmsService service;

    private final UserService userService;

    @PostMapping("/sendOtp") /* sinh mã otp */
    public ResponseEntity<?> generateOtp(@RequestBody UserRequest request) {

        String phoneOtp;
        String emailOtp;

        boolean hasEmail = request.getNewEmail() != null && !request.getNewEmail().isBlank();
        boolean hasPhone = request.getNewPhone() != null && !request.getNewPhone().isBlank();

        /* cả 2 đều rỗng */
        if (!hasEmail && !hasPhone) {
            return ResponseEntity.badRequest().body("Vui lòng nhập email mới hoặc số điện thoại mới");
        }

        /* chỉ có phone */
        if (!hasEmail) {
            phoneOtp = this.userService.generatePhoneOtp(request);
            return ResponseEntity.ok(phoneOtp);
        }

        /* chỉ có email */
        if (!hasPhone) {
            emailOtp = this.userService.generateEmailOtp(request);
            return ResponseEntity.ok(emailOtp);
        }

        /* có cả 2 */
        phoneOtp = this.userService.generatePhoneOtp(request);
        emailOtp = this.userService.generateEmailOtp(request);

        String otpCode = """
            phone otp code : %s
            email otp code : %s
            """.formatted(phoneOtp, emailOtp);

        return ResponseEntity.ok(otpCode);
    }

    /*Verify email*/
    @GetMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
            ){
        String result = this.userService.verifyOtp(email,otp);
        return ResponseEntity.ok(result);

    }
}
