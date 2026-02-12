package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.entity.*;
import fpt.ntu.vuatrovn.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("kiet@gmail.com");
        user.setPassword("oldPassword");
    }

    // =============================
    // FORGOT PASSWORD TEST
    // =============================

    @Test
    void forgotPassword_success() {

        when(userRepository.findByEmail("kiet@gmail.com"))
                .thenReturn(Optional.of(user));

        authService.forgotPassword("kiet@gmail.com");

        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1))
                .sendResetPasswordEmail(eq("kiet@gmail.com"), anyString());
    }

    @Test
    void forgotPassword_emailNotFound() {

        when(userRepository.findByEmail("notfound@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.forgotPassword("notfound@gmail.com")
        );

        assertEquals("Email not found", exception.getMessage());
    }

    // =============================
    // RESET PASSWORD TEST
    // =============================

    @Test
    void resetPassword_success() {

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("valid-token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(token));

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encodedPass");

        authService.resetPassword("valid-token", "newPass");

        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);

        assertEquals("encodedPass", user.getPassword());
    }

    @Test
    void resetPassword_invalidToken() {

        when(tokenRepository.findByToken("invalid"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.resetPassword("invalid", "123")
        );

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void resetPassword_tokenExpired() {

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("expired-token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(5));

        when(tokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(token));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.resetPassword("expired-token", "123")
        );

        assertEquals("Token expired", exception.getMessage());
    }
}
