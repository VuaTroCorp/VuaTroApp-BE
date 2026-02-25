package fpt.ntu.vuatrovn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fpt.ntu.vuatrovn.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { // Đã thêm public
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}