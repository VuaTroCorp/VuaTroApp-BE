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
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Gọi hàm loadUser của class cha để lấy thông tin từ Google
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // Lấy tên nhà cung cấp (ví dụ: "google")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        processOAuth2User(oAuth2User, registrationId);
        return oAuth2User;
    }

    public User processOAuth2User(OAuth2User oAuth2User, String registrationId) {
        String email = oAuth2User.getAttribute("email"); 
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        // Chuyển chuỗi "google" thành Enum Provider.GOOGLE
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        // Tìm kiếm user bằng Enum chuẩn
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUser.isPresent()) {
            return existingUser.get(); // Đã có tài khoản thì không tạo mới nữa
        }

        // Tạo mới tài khoản nếu chưa tồn tại
        User user = new User();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setEmail(email);
        user.setUsername(name != null ? name : email);
        user.setStatus(UserStatus.ACTIVE); 
        user.setRole(Role.USER);

        userRepository.save(user);
        System.out.println("✅ ĐÃ LƯU USER GOOGLE MỚI: " + email);
        return user;
    }
}