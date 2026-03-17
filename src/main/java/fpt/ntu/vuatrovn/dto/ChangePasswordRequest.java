package fpt.ntu.vuatrovn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "resetToken không được để trống")
    private String resetToken;

    @NotBlank(message = "new password không được để trống")
    private String newPassword;

    @NotBlank(message = "comfirm password không để trống")
    private String comfirmPassword;
}
