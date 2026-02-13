package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.AuthProvider; // Import Enum
import fpt.ntu.vuatrovn.enums.UserStatus;  // Import Enum
import fpt.ntu.vuatrovn.enums.UserRole;    // Import Enum
import fpt.ntu.vuatrovn.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        processOAuth2User(oAuth2User);
        return oAuth2User;
    }

     void processOAuth2User(OAuth2User oAuth2User) {

        String providerId = oAuth2User.getAttribute("sub");
        // Sửa lỗi: Dùng Enum AuthProvider.GOOGLE chứ không dùng chuỗi "google"
        AuthProvider provider = AuthProvider.GOOGLE; 

        // Sửa lỗi: Tìm theo Enum
        Optional<User> existingUser =
                userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUser.isPresent()) {
            return;
        }

        User user = new User();
        user.setProvider(provider); // Sửa lỗi: set bằng Enum
        user.setProviderId(providerId);
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setUsername(oAuth2User.getAttribute("name"));
        user.setStatus(UserStatus.ACTIVE); // Sửa lỗi: set bằng Enum
        user.setRole(UserRole.USER); // Mặc định là USER

        userRepository.save(user);
    }
}