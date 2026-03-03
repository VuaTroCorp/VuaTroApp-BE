package fpt.ntu.vuatrovn.entity;

import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data // Đã bao gồm toàn bộ Getter, Setter, toString...
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

    // 🔥 Đã bổ sung trường password bị thiếu
    @Column(name = "password")
    private String password;

    private String phone;

    // OAuth2
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status; 

    @Enumerated(EnumType.STRING)
    private Role role;
    
}