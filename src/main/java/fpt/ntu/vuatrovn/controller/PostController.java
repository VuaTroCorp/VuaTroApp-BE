package fpt.ntu.vuatrovn.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.ntu.vuatrovn.dto.CreatePostRequest;
import fpt.ntu.vuatrovn.service.PostService;
import fpt.ntu.vuatrovn.service.RentalRegistrationService;

@RestController
@RequestMapping("api/posts")
public class PostController {
    private final PostService postService;
    private final RentalRegistrationService rentalRegistrationService;

    public PostController(PostService postService,
                          RentalRegistrationService rentalRegistrationService) {
        this.postService = postService;
        this.rentalRegistrationService = rentalRegistrationService;
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {

        if (authentication == null) {

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("status", 401);
            response.put("message", "Bạn chưa đăng nhập");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        String email = authentication.getName();

        postService.createPost(request, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", 201);
        response.put("message", "Tạo bài đăng thành công");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/{postId}/register-view")
public ResponseEntity<?> registerViewRoom(
        @PathVariable Long postId,
        Authentication authentication
) {

    if(authentication == null){

        Map<String,Object> response = new HashMap<>();
        response.put("status",401);
        response.put("message","Bạn chưa đăng nhập");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    String email = authentication.getName();

    rentalRegistrationService.registerViewRoom(postId,email);

    Map<String,Object> response = new HashMap<>();
    response.put("status",200);
    response.put("message",
            "Your request has been sent. Please wait for landlord confirmation.");

    return ResponseEntity.ok(response);
}
}
