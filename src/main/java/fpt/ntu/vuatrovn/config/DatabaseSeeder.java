package fpt.ntu.vuatrovn.config;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.AuthProvider;
import fpt.ntu.vuatrovn.enums.UserRole;
import fpt.ntu.vuatrovn.enums.UserStatus;
import fpt.ntu.vuatrovn.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Kiểm tra xem đã có user nào chưa, nếu chưa thì tạo Admin mẫu
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setEmail("admin@vuatro.com");
                admin.setName("Super Admin");
                // Mật khẩu là 123456, mã hóa trước khi lưu
                admin.setPasswordHashed(passwordEncoder.encode("123456"));
                admin.setRole(UserRole.ADMIN);
                admin.setStatus(UserStatus.OPENED);
                admin.setProvider(AuthProvider.LOCAL);
                
                userRepository.save(admin);
                System.out.println("---------------------------------------------");
                System.out.println("DA TAO USER MAU: admin@vuatro.com / 123456");
                System.out.println("---------------------------------------------");
            }
        };
    }
}