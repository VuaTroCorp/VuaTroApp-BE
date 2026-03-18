package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.service.PostService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/home")
@Tag(name = "Home API", description = "Home")
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;
    
    @GetMapping
    public ResponseEntity<?> getAllPost(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<Post> posts = postService.getAllPosts(page, size);
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
