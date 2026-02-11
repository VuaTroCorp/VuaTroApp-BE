package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.UserStatus;
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

    // ✅ TESTABLE METHOD
     void processOAuth2User(OAuth2User oAuth2User) {

        String provider = "google";
        String providerId = oAuth2User.getAttribute("sub");

        Optional<User> existingUser =
                userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUser.isPresent()) {
            return; // ❗ user đã tồn tại → không save
        }

        User user = new User();
        user.setProvider(Provider.GOOGLE);
        user.setProviderId(providerId);
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setUsername(oAuth2User.getAttribute("name"));
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

    
}