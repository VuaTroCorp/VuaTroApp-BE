package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    private UserRepository userRepository;
    private CustomOAuth2UserService service;

    @BeforeEach 
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new CustomOAuth2UserService(userRepository);
    }

    @Test
    void processOAuth2User_shouldCreateUser_whenUserNotExists() {

        OAuth2User oAuth2User = mock(OAuth2User.class);

        when(oAuth2User.getAttribute("sub")).thenReturn("123");
        when(oAuth2User.getAttribute("email")).thenReturn("test@gmail.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Test User");

        when(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "123"))
                .thenReturn(Optional.empty());

        service.processOAuth2User(oAuth2User);

        verify(userRepository).save(argThat(user ->
                user.getProvider().equals(Provider.GOOGLE) &&
                user.getProviderId().equals("123") &&
                user.getEmail().equals("test@gmail.com") &&
                user.getUsername().equals("Test User") &&
                user.getStatus() == UserStatus.ACTIVE
        ));
    }
}
