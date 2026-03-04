package fpt.ntu.vuatrovn.controller;

import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post Search API", description = "API tìm kiếm và lọc bài đăng phòng trọ")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm bài đăng với bộ lọc động", 
               description = "Hỗ trợ lọc theo từ khóa, khoảng giá, diện tích, khu vực. Mặc định chỉ hiện bài đã APPROVE.")
    public ResponseEntity<Page<Post>> search(
            // Spring sẽ tự động ánh xạ các tham số query (VD: ?keyword=abc) vào DTO này
            PostSearchRequest searchRequest,
            
            @Parameter(description = "Số trang (bắt đầu từ 0)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Số bản ghi mỗi trang") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sắp xếp theo (VD: price,asc hoặc post_id,desc)") 
            @RequestParam(defaultValue = "id,desc") String sort) {

        // 1. Xử lý logic sắp xếp
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")) 
                                    ? Sort.Direction.ASC : Sort.Direction.DESC;

        // 2. Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // 3. Gọi service và trả về kết quả
        Page<Post> result = postService.searchPosts(searchRequest, pageable);
        return ResponseEntity.ok(result);
    }
}