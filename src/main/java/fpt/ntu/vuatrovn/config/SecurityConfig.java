package fpt.ntu.vuatrovn.config;

import fpt.ntu.vuatrovn.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired(required = false) // Để false để tránh lỗi bean
    private CustomOAuth2UserService oAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (Swagger, Auth, Home)
                .requestMatchers(
                    "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", 
                    "/h2-console/**",
                    "/api/auth/**", "/login/**", "/oauth2/**",
                    "/", "/home"
                ).permitAll()
                // Private endpoints
                .anyRequest().authenticated()
            )
            // Cấu hình OAuth2 (Google)
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/home", true)
                .userInfoEndpoint(userInfo -> {
                    if (oAuth2UserService != null) {
                        userInfo.userService(oAuth2UserService);
                    }
                })
            )
            // Fix hiển thị H2 Console
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}