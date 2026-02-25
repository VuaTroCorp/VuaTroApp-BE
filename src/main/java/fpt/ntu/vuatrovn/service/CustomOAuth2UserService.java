package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider; // Sửa AuthProvider -> Provider
import fpt.ntu.vuatrovn.enums.Role;     // Sửa UserRole -> Role
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        // Lấy provider (google, facebook...)
        String providerStr = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Provider provider = Provider.valueOf(providerStr); // Dùng Enum Provider mới

        String email = oauth2User.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        
        if(userOptional.isPresent()) {
            user = userOptional.get();
            // Update thông tin nếu cần
            user.setProvider(provider);
            userRepository.save(user);
        } else {
            // Tạo user mới
            user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setProvider(provider);
            user.setRole(Role.USER); // Dùng Enum Role mới
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }

        return UserPrincipal.create(user, oauth2User.getAttributes());
    }
}