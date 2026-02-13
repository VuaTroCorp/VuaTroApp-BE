package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.LoginRequest;
import fpt.ntu.vuatrovn.dto.LoginResponse;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.AuthProvider; // Import Enum
import fpt.ntu.vuatrovn.enums.UserRole;
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
        
        mockUser.setRole(UserRole.ADMIN);
        mockUser.setStatus(UserStatus.OPENED);
        mockUser.setProvider(AuthProvider.LOCAL); // Sửa: Dùng Enum

        // 2. Mock hành vi
        when(userRepository.findByEmail("admin@vuatro.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123456", "hashed_123456")).thenReturn(true);

        // 3. Chạy test
        LoginRequest request = new LoginRequest("admin@vuatro.com", "123456");
        LoginResponse response = authService.login(request);

        // 4. Kiểm tra
        assertNotNull(response.getToken());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void login_Fail_WrongPassword() {
        User mockUser = new User();
        mockUser.setEmail("admin@vuatro.com");
        mockUser.setPassword("hashed_123456"); // SỬA
        mockUser.setProvider(AuthProvider.LOCAL); // SỬA

        when(userRepository.findByEmail("admin@vuatro.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong_pass", "hashed_123456")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.login(new LoginRequest("admin@vuatro.com", "wrong_pass"));
        });

        assertEquals("Mật khẩu không chính xác", exception.getMessage());
    }
}