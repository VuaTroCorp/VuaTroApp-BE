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
            // 🔥 Code của team: Tắt CSRF và bật CORS cho REST API
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) 

            // 🔥 QUAN TRỌNG (Của bạn): Đổi sang IF_REQUIRED để Google OAuth2 có thể lưu Session xác thực
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            // 🔥 Phân quyền API (Đã gộp đường dẫn của team và của bạn)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/", "/home", "/login/**", "/oauth2/**", 
                    "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", 
                    "/swagger-ui.html", "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 🔥 Code của bạn: Xử lý Đăng nhập Google
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(withDefaults())
                .successHandler((request, response, authentication) -> {
                    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                    String registrationId = ((OAuth2AuthenticationToken) authentication)
                            .getAuthorizedClientRegistrationId();

                    customOAuth2UserService.processOAuth2User(oAuth2User, registrationId);
                    
                    response.sendRedirect("/");
                })
                .failureHandler((request, response, exception) -> {
                    System.err.println("❌ LỖI ĐĂNG NHẬP: " + exception.getMessage());
                    response.sendRedirect("/login?error");
                })
            )
            
            // Cho phép hiển thị giao diện H2 Console
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // 🔥 Code của team: Cấu hình CORS chuẩn
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(false);
        config.setAllowedOriginPatterns(List.of("*")); 
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));

        // Đã sửa lại lỗi khai báo biến ở đây
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}