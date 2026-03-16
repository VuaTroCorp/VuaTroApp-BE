package fpt.ntu.vuatrovn.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fpt.ntu.vuatrovn.dto.CreateReviewRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.Review;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.repository.ReviewRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // Tiêm các Repository vào thông qua Constructor
    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         PostRepository postRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

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
}