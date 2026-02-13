package fpt.ntu.vuatrovn.entity;

import fpt.ntu.vuatrovn.enums.AuthProvider; // Import Enum
import fpt.ntu.vuatrovn.enums.UserRole;
import fpt.ntu.vuatrovn.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "password_hashed")
    private String password;

    @Column(name = "phone")
    private String phone;

    // --- QUAN TRỌNG: Phải là AuthProvider, không được là String ---
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private AuthProvider provider; 

    @Column(name = "provider_ID")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status; 

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;
}