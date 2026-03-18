package fpt.ntu.vuatrovn.repository;

import fpt.ntu.vuatrovn.entity.OtpVerifications;
import fpt.ntu.vuatrovn.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerifications,Long> {
    Optional<OtpVerifications> findByTargetValue(String targetValue);

    Optional<OtpVerifications> findByTargetValueAndOtpAndType(String targetValue, String otp, OtpType type);

}
