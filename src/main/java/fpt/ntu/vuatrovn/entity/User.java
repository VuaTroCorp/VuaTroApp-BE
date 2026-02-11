package fpt.ntu.vuatrovn.entity;

import fpt.ntu.vuatrovn.enums.*; // Import 3 cái Enum vừa tạo
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // Đặt tên bảng là 'users' (số nhiều) để tránh lỗi từ khóa SQL
@Data                  // Lombok tự sinh Getter/Setter
@NoArgsConstructor     // Lombok tự sinh Constructor không tham số
@AllArgsConstructor    // Lombok tự sinh Constructor đầy đủ tham số
@Builder               // Giúp tạo đối tượng User dễ hơn (dùng trong Test)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID (1, 2, 3...)
    @Column(name = "user_ID")
    private Integer userId;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true, nullable = false) // Email không được trùng, không được rỗng
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password_hashed")
    private String passwordHashed;

    // --- Sử dụng 3 Enum vừa tạo ở đây ---

    @Enumerated(EnumType.STRING) // Lưu vào DB dưới dạng chữ (ví dụ: "LOCAL", "GOOGLE")
    @Column(name = "provider")
    private AuthProvider provider;

    @Column(name = "provider_ID")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status; // OPENED, LOCK

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role; // ADMIN, USER
}