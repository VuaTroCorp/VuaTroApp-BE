package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
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
        // Lấy tên nhà cung cấp (ví dụ: "google")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        processOAuth2User(oAuth2User, registrationId);
        return oAuth2User;
    }

    // Đổi thành public và thêm tham số registrationId để SecurityConfig có thể gọi
    public void processOAuth2User(OAuth2User oAuth2User, String registrationId) {

        // Chuyển chuỗi "google" thành Enum Provider.GOOGLE
        Provider provider = Provider.valueOf(registrationId.toUpperCase());
        String providerId = oAuth2User.getAttribute("sub");

        // Tìm kiếm user bằng Enum chuẩn
        Optional<User> existingUser =
                userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUser.isPresent()) {
            return; // Đã có tài khoản thì không tạo mới nữa
        }

        // Tạo mới tài khoản
        User user = new User();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setEmail(oAuth2User.getAttribute("email"));
        
        // Google có thể trả về name hoặc mặc định lấy email làm username
        String name = oAuth2User.getAttribute("name");
        user.setUsername(name != null ? name : oAuth2User.getAttribute("email"));
        
        user.setStatus(UserStatus.ACTIVE); 
        user.setRole(Role.USER); // Dùng Role chuẩn

        userRepository.save(user);
    }
}