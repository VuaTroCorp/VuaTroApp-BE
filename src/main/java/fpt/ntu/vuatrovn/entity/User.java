package fpt.ntu.vuatrovn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

// 🔥 CÁC DÒNG IMPORT QUAN TRỌNG ĐÃ ĐƯỢC BỔ SUNG
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;
import jakarta.persistence.*; // Dành cho @Entity, @Id, @Column...
import lombok.*;            // Dành cho @Data, @Builder...

@Entity
@Table(name = "users")
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status; 

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user")
    private PasswordReset passwordReset;
}