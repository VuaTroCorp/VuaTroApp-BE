package fpt.ntu.vuatrovn.config;
import java.util.List;

import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.security.JwtAuthenticationFilter;
import fpt.ntu.vuatrovn.service.CustomOAuth2UserService;
import fpt.ntu.vuatrovn.service.JwtService;
import jakarta.servlet.http.HttpServletResponse; // 🔥 Import quan trọng
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                      JwtAuthenticationFilter jwtAuthenticationFilter,
                      JwtService jwtService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔥 PHẦN SỬA LỖI: Trả về 403 thay vì redirect 302
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Access Denied: Please login first.");
                })
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/", "/home", "/login/**", "/oauth2/**",
                    "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", 
                    "/swagger-ui.html", "/h2-console/**","/api/test/**", "/api/posts/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class)
                
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(withDefaults())
            .successHandler((request, response, authentication) -> {

                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                String registrationId = ((OAuth2AuthenticationToken) authentication)
                        .getAuthorizedClientRegistrationId();

                    User user = customOAuth2UserService.processOAuth2User(oAuth2User, registrationId);

                    String token = jwtService.generateToken(
                            user.getEmail(),
                            user.getUsername(),
                            user.getRole().name()
                    );

                response.setContentType("application/json");
                response.getWriter().write("{\"token\":\"" + token + "\"}");
            })
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
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