package fpt.ntu.vuatrovn.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fpt.ntu.vuatrovn.dto.CreatePostRequest;
import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.service.PostService;
import fpt.ntu.vuatrovn.service.SupabaseStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post API", description = "API quản lý bài đăng và upload ảnh")
public class PostController {

    private final PostService postService;
    private final SupabaseStorageService storageService; // 🔥 Đã bổ sung biến này

    // 🔥 Đã tiêm ĐÚNG cả 2 Service vào Constructor
    public PostController(PostService postService, SupabaseStorageService storageService) {
        this.postService = postService;
        this.storageService = storageService;
    }

    // ==========================================
    // 1. API TẠO BÀI ĐĂNG
    // ==========================================
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @ModelAttribute CreatePostRequest request,
            Authentication authentication
    ) {
        // ⚠️ LƯU Ý: Phần code bên trong hàm này của bạn đã bị Git xóa mất lúc gộp code.
        // Tạm thời trả về Mock Data để Build Success.
        Map<String, String> response = new HashMap<>();
        response.put("message", "API Create Post đang được hoàn thiện");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    // 3. API TEST UPLOAD (Của đồng đội - khôi phục lại)
    // ==========================================
    @GetMapping("/test-auth")
    @Operation(summary = "Test xác thực người dùng")
    public String testAuth(Authentication authentication) {
        return "Current user: " + authentication.getName();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file lên Supabase")
    public String upload(@RequestPart("file") MultipartFile file) throws IOException {
        return storageService.uploadFile(file);
    }
}