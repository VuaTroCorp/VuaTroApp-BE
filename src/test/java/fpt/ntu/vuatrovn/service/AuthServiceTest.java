package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.LoginRequest;
import fpt.ntu.vuatrovn.dto.LoginResponse;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider; 
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_Success() {
        // 1. Chuẩn bị dữ liệu giả
        User mockUser = new User();
        mockUser.setEmail("admin@vuatro.com");
        mockUser.setPassword("hashed_123456"); 
        
        // SỬA: Đã dùng đúng Enum chuẩn
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
    mockUser.setRole(Role.ADMIN);
    mockUser.setStatus(UserStatus.ACTIVE);
    mockUser.setProvider(Provider.LOCAL);

    when(userRepository.findByEmail("admin@vuatro.com"))
            .thenReturn(Optional.of(mockUser));

    when(passwordEncoder.matches("123456", "hashed_123456"))
            .thenReturn(true);

    when(jwtService.generateToken("admin@vuatro.com"))
            .thenReturn("test_token_value");

    LoginResponse response =
            authService.login(new LoginRequest("admin@vuatro.com", "123456"));

    assertEquals("test_token_value", response.getToken());
    assertEquals("ADMIN", response.getRole());
    }
}