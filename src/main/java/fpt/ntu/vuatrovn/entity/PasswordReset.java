package fpt.ntu.vuatrovn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String otp;

    private Instant otp_expiry;

    private String resetToken;

    private Instant token_expiry;

    private boolean verified;

    private boolean used;

    private int countTryOtp;

    private boolean block;

//    1️⃣ Khóa ngoại
    @OneToOne
    private User user;


}
