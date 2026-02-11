package fpt.ntu.vuatrovn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Tự động sinh Constructor có đầy đủ tham số
public class LoginResponse {
    private String token;   // Vé thông hành
    private String role;    // Chức vụ (ADMIN/USER)
    private String message; // Lời nhắn (Ví dụ: "Đăng nhập thành công")
}