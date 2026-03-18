package fpt.ntu.vuatrovn.config;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.Provider; 
import fpt.ntu.vuatrovn.enums.Role;     
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
                User admin = new User();
                admin.setEmail("admin@vuatro.com");
                
                admin.setUsername("Super Admin"); 
                admin.setPassword(passwordEncoder.encode("123456")); 
                
                // Đã cập nhật lại các Enum theo code mới chuẩn hóa
                admin.setRole(Role.ADMIN); 
                admin.setStatus(UserStatus.ACTIVE); 
                admin.setProvider(Provider.LOCAL); 
                
                userRepository.save(admin);
                System.out.println("---------------------------------------------");
                System.out.println("DA TAO USER MAU: admin@vuatro.com / 123456");
                System.out.println("---------------------------------------------");
        };
    }
}