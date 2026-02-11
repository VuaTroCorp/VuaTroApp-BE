package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.*;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.*;
import fpt.ntu.vuatrovn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // 2. Kiểm tra Provider (Chỉ LOCAL mới được login bằng pass)
        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new RuntimeException("Vui lòng đăng nhập bằng Google");
        }

        // 3. Kiểm tra mật khẩu (So sánh pass nhập vào vs pass trong DB)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHashed())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        // 4. Kiểm tra trạng thái
        if (user.getStatus() == UserStatus.LOCK) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // 5. Sinh Token giả lập (Thực tế sẽ dùng JWT)
        String token = UUID.randomUUID().toString();

        return new LoginResponse(token, user.getRole().name(), "Đăng nhập thành công");
    }
}