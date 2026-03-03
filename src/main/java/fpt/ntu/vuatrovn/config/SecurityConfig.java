package fpt.ntu.vuatrovn.config;

import fpt.ntu.vuatrovn.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Tắt CSRF vì chúng ta làm REST API
            .csrf(csrf -> csrf.disable())
            
            // 2. Cấu hình CORS (Lấy từ Bean corsConfigurationSource bên dưới)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. Quản lý Session: OAuth2 cần Session để duy trì đăng nhập
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            // 4. Phân quyền API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Hỗ trợ CORS pre-flight
                .requestMatchers(
                    "/", "/home", "/login/**", "/oauth2/**", 
                    "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", 
                    "/swagger-ui.html", "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 5. Cấu hình Google OAuth2 Login
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(withDefaults())
                .successHandler((request, response, authentication) -> {
                    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                    String registrationId = ((OAuth2AuthenticationToken) authentication)
                            .getAuthorizedClientRegistrationId();

                    // Gọi service để lưu user vào Database
                    customOAuth2UserService.processOAuth2User(oAuth2User, registrationId);
                    
                    response.sendRedirect("/");
                })
                .failureHandler((request, response, exception) -> {
                    System.err.println("❌ LỖI ĐĂNG NHẬP GOOGLE: " + exception.getMessage());
                    response.sendRedirect("/login?error");
                })
            )
            
            // 6. Cho phép H2 Console hiển thị trong Frame
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // 🔥 Cấu hình CORS chuẩn để Frontend có thể gọi API
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi Cookie/Token
        config.setAllowedOriginPatterns(List.of("*")); 
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}