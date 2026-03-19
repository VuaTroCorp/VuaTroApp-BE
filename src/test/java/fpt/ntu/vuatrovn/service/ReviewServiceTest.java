package fpt.ntu.vuatrovn.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import fpt.ntu.vuatrovn.dto.CreateReviewRequest;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    // @Mock
    // private ReviewRepository reviewRepository;
    // ... mock các repo khác

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReview_Fail_WhenRatingIsInvalid() {
        // Chuẩn bị dữ liệu sai: Rating = 6 (vượt quá 5 sao)
        CreateReviewRequest request = new CreateReviewRequest();
        request.setPostId(1L);
        request.setRating(6); 
        request.setContent("Quá tuyệt vời!");

        // Kiểm tra xem hệ thống có ném ra lỗi Bad Request không
        assertThrows(ResponseStatusException.class, 
            () -> reviewService.createReview(request, "test@gmail.com"));
    }

    @Test
    void createReview_Fail_WhenContentIsEmpty() {
        // Chuẩn bị dữ liệu sai: Bỏ trống nội dung
        CreateReviewRequest request = new CreateReviewRequest();
        request.setPostId(1L);
        request.setRating(5);
        request.setContent(""); 

        assertThrows(ResponseStatusException.class, 
            () -> reviewService.createReview(request, "test@gmail.com"));
    }
}