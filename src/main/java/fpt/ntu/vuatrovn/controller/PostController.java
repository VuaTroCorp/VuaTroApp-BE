package fpt.ntu.vuatrovn.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.ntu.vuatrovn.dto.CreatePostRequest;
import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.dto.UpdatePostRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "Manage posts an upload images API")
public class PostController {

    private final PostService postService;

    // ==========================================
    // 1. API TẠO BÀI ĐĂNG
    // ==========================================
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create post", description = "Create a post with information about the room")
    public ResponseEntity<?> createPost(
            @ModelAttribute CreatePostRequest request,
            Authentication authentication
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

        postService.createPost(request, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", 201);
        response.put("message", "Create post successfully");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // 2. API TÌM KIẾM BÀI ĐĂNG (Của bạn)
    // ==========================================
    @GetMapping("/search")
    @Operation(summary = "Search for rooms using automatic filters.", 
               description = "Supports filtering by keyword, price range, area, and location.")
    public ResponseEntity<Page<Post>> search(
            PostSearchRequest searchRequest,
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số bản ghi mỗi trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo (VD: id,desc)") @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")) 
                                    ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Post> result = postService.searchPosts(searchRequest, pageable);
        
        return ResponseEntity.ok(result);
    }

    // ==========================================
    // 3. GET POST DETAIL
    // ==========================================  
    @GetMapping("/{id}")
    @Operation(summary = "Get post detail", description = "Choose a post to get information about it.")
    public ResponseEntity<Post> getPostDetail(@PathVariable Long id) {

        Post post = postService.getPostDetail(id);

        return ResponseEntity.ok(post);
    }


    // ==========================================
    // 4. EDIT POST API
    // ==========================================   
    @PostMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Edit Post", description = "Choose a post to edit content of that post.")
    public ResponseEntity<?> updatePost(
            @Parameter(description = "Post ID") 
            @PathVariable Long id,
            @ModelAttribute UpdatePostRequest request,
            Authentication authentication
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

        postService.updatePost(id, request, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", 200);
        response.put("message", "Update post successfully");

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 5. DELETE POST API
    // ========================================== 
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete post", description = "Choose a post to delete it (Soft Delete)")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication){
        String email = authentication.getName();
        postService.deletePost(id, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", 200);
        response.put("message", "Delete post successfully");
        return ResponseEntity.ok(response);
    }
}
