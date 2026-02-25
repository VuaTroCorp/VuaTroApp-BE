package fpt.ntu.vuatrovn.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/h2-console/**",
                    "/swagger-ui.html",
                    "/api/auth/**" // Code API Đăng ký của nhóm bạn
                ).permitAll()
                .anyRequest().authenticated()
            )

            // ✅ Form login mặc định (Của nhóm)
            .formLogin(withDefaults())

            // ✅ TÍNH NĂNG GOOGLE LOGIN (Của bạn thêm vào)
            .oauth2Login(withDefaults())

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )

            .headers(headers ->
                headers.frameOptions(frame -> frame.disable()) // Cho phép hiển thị giao diện H2 Database
            );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        // Sửa lỗi CORS của Spring Boot 3: Dùng AllowedOriginPatterns thay vì AllowedOrigins khi bật Credentials
        config.setAllowedOriginPatterns(List.of("*")); 
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Mã hóa mật khẩu cho tính năng Đăng ký
    }
}