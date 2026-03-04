package fpt.ntu.vuatrovn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpRequest {
    @NotBlank(message = "email không để trống")
    private String email;
    @NotBlank(message = "otp code không để trống")
    private String otpCode;
}
