package fpt.ntu.vuatrovn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.ntu.vuatrovn.dto.CreateReviewRequest;
import fpt.ntu.vuatrovn.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review API", description = "API Đánh giá và Bình luận bài đăng")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    @Operation(summary = "Tạo bình luận & đánh giá mới", 
               description = "Người dùng phải đăng nhập mới có thể đánh giá bài viết.")
    public ResponseEntity<?> createReview(
            @RequestBody CreateReviewRequest request,
            Authentication authentication) {
        
        // Lấy email người dùng đang đăng nhập (nhờ JWT Token)
        String userEmail = authentication.getName();
        
        reviewService.createReview(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đánh giá thành công!");
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Lấy danh sách đánh giá của một bài đăng",
               description = "Ai cũng có thể xem đánh giá, không cần đăng nhập.")
    public ResponseEntity<?> getReviewsByPost(@PathVariable Long postId) {
        // Gọi xuống Service để lấy danh sách review của bài đăng này
        return ResponseEntity.ok(reviewService.getReviewsByPostId(postId));
    }
}