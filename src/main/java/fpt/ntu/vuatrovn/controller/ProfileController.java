package fpt.ntu.vuatrovn.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile API", description = "Manage user accounts and posts.")
@RequiredArgsConstructor
public class ProfileController {
    private final PostService postService;

    // ==========================================
    // . GET USER'S POSTS
    // ==========================================
    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(
        Authentication authentication,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        if (authentication == null) {

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("status", 401);
            response.put("message", "User not logged in");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        String email = authentication.getName();

        Page<Post> posts = postService.getMyPosts(email, page, size);

        return ResponseEntity.ok(formatResponse(posts));
    }

    private Map<String, Object> formatResponse(Page<Post> posts) {
        Map<String, Object> response = new HashMap<>();

        response.put("content", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());

        return response;
    }
}
