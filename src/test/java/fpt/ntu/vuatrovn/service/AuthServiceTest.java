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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        // tạo user giả
        User mockUser = new User();
        mockUser.setEmail("admin@vuatro.com");
        mockUser.setPassword("hashed_123456");
        mockUser.setRole(Role.ADMIN);
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setProvider(Provider.LOCAL);

        // mock repository
        when(userRepository.findByEmail("admin@vuatro.com"))
                .thenReturn(Optional.of(mockUser));

        // mock password check
        when(passwordEncoder.matches("123456", "hashed_123456"))
                .thenReturn(true);

        // mock jwt
        when(jwtService.generateToken(any(), any(), any()))
                .thenReturn("test_token_value");

        // request login
        LoginRequest request = new LoginRequest("admin@vuatro.com", "123456");

        // gọi service
        LoginResponse response = authService.login(request);

        // kiểm tra
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("ADMIN", response.getRole());

        // verify method được gọi
        verify(userRepository).findByEmail("admin@vuatro.com");
        verify(passwordEncoder).matches("123456", "hashed_123456");
        verify(jwtService).generateToken(any(), any(), any());
    }

    @Test
    void login_Fail_WrongPassword() {

        User mockUser = new User();
        mockUser.setEmail("admin@vuatro.com");
        mockUser.setPassword("hashed_123456");
        mockUser.setProvider(Provider.LOCAL);
        mockUser.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByEmail("admin@vuatro.com"))
                .thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches("wrong_pass", "hashed_123456"))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(new LoginRequest("admin@vuatro.com", "wrong_pass"))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Email hoặc mật khẩu không chính xác", exception.getReason());
    }

    @Test
    void login_Fail_UserNotFound() {

        when(userRepository.findByEmail("admin@vuatro.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(new LoginRequest("admin@vuatro.com", "123456"))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}