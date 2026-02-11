package fpt.ntu.vuatrovn.repository;

import fpt.ntu.vuatrovn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<User, Integer>: Quản lý bảng User, khóa chính là Integer
public interface UserRepository extends JpaRepository<User, Integer> {

    // Đây là hàm "thần thánh": Tự động tìm User có email trùng với tham số truyền vào
    // Optional<User>: Nghĩa là kết quả có thể có User hoặc rỗng (nếu không tìm thấy)
    Optional<User> findByEmail(String email);
}