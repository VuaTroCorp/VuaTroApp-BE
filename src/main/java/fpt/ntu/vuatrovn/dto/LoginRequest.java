package fpt.ntu.vuatrovn.dto;

import lombok.AllArgsConstructor; // 1. Thêm dòng này
import lombok.Data;
import lombok.NoArgsConstructor;  // 2. Thêm dòng này
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor // 3. Giúp tạo new LoginRequest("email", "pass")
@NoArgsConstructor  // 4. Giúp tạo new LoginRequest() rỗng
public class LoginRequest {
    
    @NotBlank(message = "Email không được để trống")
    private String email;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}