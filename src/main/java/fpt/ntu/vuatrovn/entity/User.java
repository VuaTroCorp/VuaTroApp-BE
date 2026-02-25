package fpt.ntu.vuatrovn.entity;

import jakarta.persistence.*;
import lombok.*; // QUAN TRỌNG: Thêm dòng này để dùng @Data, @Builder
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;

@Entity
@Table(name = "users")
@Data                 // Tự sinh Getter, Setter, toString...
@NoArgsConstructor    // Tự sinh Constructor không tham số
@AllArgsConstructor   // Tự sinh Constructor full tham số
@Builder              // Hỗ trợ Builder pattern
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // QUAN TRỌNG: Đã thêm lại trường password bị thiếu
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

    // Đã xóa hết các hàm Getter/Setter thủ công ở dưới 
    // vì @Data đã tự động làm việc đó rồi!
}