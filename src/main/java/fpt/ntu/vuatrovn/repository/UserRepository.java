package fpt.ntu.vuatrovn.repository;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo Email (dùng cho cả đăng nhập thường và Google)
    Optional<User> findByEmail(String email);

    // Tìm user theo Username
    Optional<User> findByUsername(String username);

    // Kiểm tra xem Email đã tồn tại chưa (dùng khi đăng ký)
    boolean existsByEmail(String email);

    // Kiểm tra xem Username đã tồn tại chưa (dùng khi đăng ký)
    boolean existsByUsername(String username);

    // Tìm user từ Google OAuth2 (Đã chuẩn hóa dùng Enum Provider)
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
}