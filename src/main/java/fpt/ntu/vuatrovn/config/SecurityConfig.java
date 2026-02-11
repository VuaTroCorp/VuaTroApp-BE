package fpt.ntu.vuatrovn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Tắt bảo mật CSRF để dễ test API
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập Swagger (Tài liệu API)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Cho phép truy cập API Login
                .requestMatchers("/api/auth/**").permitAll()
                // Cho phép truy cập H2 Console (Database)
                .requestMatchers("/h2-console/**").permitAll()
                // Các request khác bắt buộc phải đăng nhập
                .anyRequest().authenticated()
            )
            // Cấu hình để H2 Console hiển thị được (do H2 dùng iframe)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}