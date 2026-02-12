package fpt.ntu.vuatrovn.config;

import fpt.ntu.vuatrovn.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;    


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomOAuth2UserService oAuth2UserService
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/home",
                    "/oauth2/**",
                    "/login/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/h2-console/**",
                    "/api/auth/signup",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 🔑 GOOGLE LOGIN
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/home", true) // ⭐ quan trọng
                .userInfoEndpoint(userInfo ->
                    userInfo.userService(oAuth2UserService)
                )
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )

            .headers(headers ->
                headers.frameOptions(frame -> frame.disable())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}