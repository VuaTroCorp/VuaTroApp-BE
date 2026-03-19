package fpt.ntu.vuatrovn.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fpt.ntu.vuatrovn.dto.CreateReviewRequest;
import fpt.ntu.vuatrovn.dto.UpdateReviewRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.Review;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.repository.ReviewRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void createReview(CreateReviewRequest request, String email) {
        // 1. Validate dữ liệu đầu vào (Rating từ 1-5, Content không được rỗng)
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Điểm đánh giá phải từ 1 đến 5 sao");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nội dung bình luận không được để trống");
        }
        
        
        // 2. Tìm User và Post trong Database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin người dùng"));
                
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài đăng này"));
        
        // trùng lặp đánh giá bài đăng cùng 1 người dùng
        if (reviewRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Bạn đã đánh giá bài đăng này rồi. Xin vui lòng sử dụng chức năng Sửa đánh giá!");
        }
        // 3. Khởi tạo đối tượng Review và map dữ liệu
        Review review = new Review();
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setUser(user);
        review.setPost(post);
        review.setCreatedAt(LocalDateTime.now()); // Lưu lại thời gian đánh giá

        // 4. Lưu vào Database
        reviewRepository.save(review);
    }
    
    // ==========================================
    // API LẤY DANH SÁCH BÌNH LUẬN (Dành cho Frontend hiển thị)
    // ==========================================
    public List<Review> getReviewsByPostId(Long postId) {
        return reviewRepository.findByPostId(postId); 
    }
    // ==========================================
    // SỬA BÌNH LUẬN (Chỉ chính chủ mới được sửa)
    // ==========================================
    public void updateReview(Long reviewId, UpdateReviewRequest request, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá này"));

        // Kiểm tra xem người đang sửa có phải là tác giả của đánh giá không
        if (!review.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa đánh giá của người khác");
        }

        // Validate dữ liệu mới
        if (request.getRating() != null && request.getRating() >= 1 && request.getRating() <= 5) {
            review.setRating(request.getRating());
        }
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            review.setContent(request.getContent());
        }

        reviewRepository.save(review);
    }

    // ==========================================
    // XÓA BÌNH LUẬN (Chỉ chính chủ mới được xóa)
    // ==========================================
    public void deleteReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá này"));

        if (!review.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa đánh giá của người khác");
        }

        reviewRepository.delete(review);
    }
}