package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.LoginRequest;
import fpt.ntu.vuatrovn.dto.LoginResponse;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.*;
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
        // Giả lập dữ liệu
        User mockUser = new User();
        mockUser.setEmail("admin@gmail.com");
        mockUser.setPasswordHashed("hashed_pass");
        mockUser.setProvider(AuthProvider.LOCAL);
        mockUser.setStatus(UserStatus.OPENED);
        mockUser.setRole(UserRole.ADMIN);

        // Giả lập hành vi của Mock
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123456", "hashed_pass")).thenReturn(true);

        // Gọi hàm test
        LoginResponse response = authService.login(new LoginRequest("admin@gmail.com", "123456"));

        // Kiểm tra kết quả
        assertEquals("ADMIN", response.getRole());
        assertNotNull(response.getToken());
    }
    
    @Test
    void login_Fail_WrongPassword() {
        User mockUser = new User();
        mockUser.setEmail("admin@gmail.com");
        mockUser.setPasswordHashed("hashed_pass");
        mockUser.setProvider(AuthProvider.LOCAL);

        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong_pass", "hashed_pass")).thenReturn(false);

        // Mong đợi ném ra lỗi
        assertThrows(RuntimeException.class, () -> {
            authService.login(new LoginRequest("admin@gmail.com", "wrong_pass"));
        });
    }
}