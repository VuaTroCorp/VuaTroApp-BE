package fpt.ntu.vuatrovn.config;

import fpt.ntu.vuatrovn.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

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
            // 🔥 REST API => disable CSRF
            .csrf(csrf -> csrf.disable())

            // 🔥 Enable CORS
            .cors(cors -> {})

            // 🔥 REST API => No Session
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔥 Authorization
            .authorizeHttpRequests(auth -> auth

                // Cho phép preflight request
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public APIs
                .requestMatchers(
                        "/api/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/h2-console/**"
                ).permitAll()

                // Các request còn lại cần login
                .anyRequest().authenticated()
            )

            // Cho H2 console
            .headers(headers ->
                headers.frameOptions(frame -> frame.disable())
            );

        return http.build();
    }

    // 🔥 CORS Configuration chuẩn
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(false);
        config.setAllowedOriginPatterns(List.of("*")); // dùng pattern thay vì origins
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}