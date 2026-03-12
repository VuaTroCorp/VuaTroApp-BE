package fpt.ntu.vuatrovn.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import fpt.ntu.vuatrovn.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    public OAuth2LoginSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        // 1. Lấy email từ Google
        String email = user.getAttribute("email");
        
        // 2. Lấy tên hiển thị từ Google để làm username
        String name = user.getAttribute("name");
        
        // 3. Mặc định cấp quyền "USER" cho người đăng nhập bằng Google
        String role = "USER";

        // 🔥 Đã sửa lỗi: Truyền đầy đủ 3 tham số (email, username, role)
        String token = jwtService.generateToken(email, name, role);

        String redirectUrl = "http://localhost:3000/login/oauth2/code/google?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}