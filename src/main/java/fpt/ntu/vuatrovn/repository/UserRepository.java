package fpt.ntu.vuatrovn.repository;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo email (để đăng nhập)
    Optional<User> findByEmail(String email);

    // Kiểm tra email đã tồn tại chưa (QUAN TRỌNG: Bạn đang thiếu cái này!)
    boolean existsByEmail(String email);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Tìm user theo Provider (Google/Facebook)
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
}