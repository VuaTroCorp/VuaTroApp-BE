package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.LoginRequest;
import fpt.ntu.vuatrovn.dto.LoginResponse;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider; // Đã sửa thành Provider
import fpt.ntu.vuatrovn.enums.Role; // Đã sửa thành Role
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_Success() {
        // 1. Chuẩn bị dữ liệu giả
        User mockUser = new User();
        mockUser.setEmail("admin@vuatro.com");
        
        // SỬA: Dùng setPassword thay vì setPasswordHashed
        mockUser.setPassword("hashed_123456"); 
        
        // Đã cập nhật lại các Enum theo code mới của nhóm
        mockUser.setRole(Role.ADMIN);
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setProvider(Provider.LOCAL); 

        // 2. Mock hành vi
        when(userRepository.findByEmail("admin@vuatro.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123456", "hashed_123456")).thenReturn(true);

        // 3. Chạy test
        LoginRequest request = new LoginRequest("admin@vuatro.com", "123456");
        
        // LƯU Ý: Chỗ này có thể vẫn báo đỏ nếu nhóm bạn đã đổi tên hàm login
        LoginResponse response = authService.login(request);

        // 4. Kiểm tra
        assertNotNull(response.getToken());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void login_Fail_WrongPassword() {
        User mockUser = new User();
        mockUser.setEmail("admin@vuatro.com");
        mockUser.setPassword("hashed_123456"); 
        mockUser.setProvider(Provider.LOCAL); // Đã cập nhật

        when(userRepository.findByEmail("admin@vuatro.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong_pass", "hashed_123456")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            // LƯU Ý: Chỗ này có thể vẫn báo đỏ nếu nhóm bạn đã đổi tên hàm login
            authService.login(new LoginRequest("admin@vuatro.com", "wrong_pass"));
        });

        assertEquals("Mật khẩu không chính xác", exception.getMessage());
    }
}