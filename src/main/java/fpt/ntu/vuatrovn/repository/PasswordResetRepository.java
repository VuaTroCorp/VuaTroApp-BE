package fpt.ntu.vuatrovn.repository;

import fpt.ntu.vuatrovn.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset,Long> {

    Optional<PasswordReset> findByUser_Email(String userEmail);

    Optional<PasswordReset> findByResetToken(String resetToken);

    PasswordReset findByOtp(String otp);

}
