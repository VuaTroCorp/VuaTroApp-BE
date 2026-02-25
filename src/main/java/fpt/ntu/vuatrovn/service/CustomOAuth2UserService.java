package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService {
    private final UserRepository userRepository;

    public void processOAuth2User(OAuth2User oAuth2User, String registrationId) {
        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElse(new User());
        
        if (!userOptional.isPresent()) {
            user.setEmail(email);
            user.setUsername(name != null ? name : email);
            user.setRole(Role.USER);
            user.setStatus(UserStatus.ACTIVE);
            user.setPassword(""); 
        }
        user.setProvider(provider);
        user.setProviderId(providerId);
        
        userRepository.save(user);
        System.out.println("✅ ĐÃ LƯU USER GOOGLE: " + email);
    }
}