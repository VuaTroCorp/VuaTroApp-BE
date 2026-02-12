package fpt.ntu.vuatrovn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import fpt.ntu.vuatrovn.entity.PasswordResetToken;

public interface PasswordResetTokenRepository 
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
}
