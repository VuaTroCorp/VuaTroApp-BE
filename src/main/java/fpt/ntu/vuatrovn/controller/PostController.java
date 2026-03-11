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

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post API", description = "API quản lý bài đăng và upload ảnh")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ==========================================
    // 1. API TẠO BÀI ĐĂNG
    // ==========================================
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @ModelAttribute CreatePostRequest request,
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
    // ==========================================
    // 2. API TÌM KIẾM BÀI ĐĂNG (Của bạn)
    // ==========================================
    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm bài đăng với bộ lọc động", 
               description = "Hỗ trợ lọc theo từ khóa, khoảng giá, diện tích, khu vực.")
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
    public ResponseEntity<Post> getPostDetail(@PathVariable Long id) {

        Post post = postService.getPostDetail(id);

        return ResponseEntity.ok(post);
    }


    // ==========================================
    // 4. EDIT POST API
    // ==========================================   
    @PostMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(
            @Parameter(description = "ID bài đăng") 
            @PathVariable Long id,
            @ModelAttribute UpdatePostRequest request,
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

        postService.updatePost(id, request, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", 200);
        response.put("message", "Cập nhật bài đăng thành công");

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 5. DELETE POST API
    // ========================================== 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication){
        String email = authentication.getName();
        postService.deletePost(id, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", 200);
        response.put("message", "Cập nhật bài đăng thành công");
        return ResponseEntity.ok(response);
    }
}