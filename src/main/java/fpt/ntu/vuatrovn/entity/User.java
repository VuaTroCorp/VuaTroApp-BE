package fpt.ntu.vuatrovn.entity;

import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // ĐÂY CHÍNH LÀ BIẾN BỊ THIẾU GÂY RA LỖI:
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