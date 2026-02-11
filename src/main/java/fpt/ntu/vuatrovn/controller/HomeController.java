package fpt.ntu.vuatrovn.controller;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Controller
@Tag(name = "Authentication", description = "Google OAuth2 Login APIs")
public class HomeController {
    @Operation(
        summary = "Trang sau khi login Google",
        description = "Trả về thông tin người dùng sau khi login Google OAuth2"
    )
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User principal) {

    if (principal == null) {
        return "redirect:/login";
    }

    String email = principal.getAttribute("email");
    String name = principal.getAttribute("name");

    return "home"; // hoặc view name
}
    }

