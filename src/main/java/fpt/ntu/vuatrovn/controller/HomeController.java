package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.service.PostService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/home")
@Tag(name = "Home API", description = "Home")
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;
    
    @GetMapping
    public List<Post> getAllPost() {
        return postService.getAllPosts();
    }
}
