package fpt.ntu.vuatrovn.repository;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.AuthProvider; // <--- QUAN TRỌNG: Dòng này đang thiếu
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Của bạn
    Optional<User> findByEmail(String email);

    // Của nhóm
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);

    // Sửa lại: Dùng AuthProvider (Enum) thay vì String
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByUsername(String username);
}