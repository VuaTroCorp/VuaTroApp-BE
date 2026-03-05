package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void searchPosts_ShouldReturnPageOfPosts() {
        // 1. Chuẩn bị dữ liệu đầu vào (Arrange)
        PostSearchRequest request = new PostSearchRequest();
        request.setKeyword("Nha Trang");
        Pageable pageable = PageRequest.of(0, 10);
        
        // Giả lập Database trả về một trang rỗng
        Page<Post> mockPage = new PageImpl<>(Collections.emptyList());
        when(postRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        // 2. Thực thi hàm cần test (Act)
        Page<Post> result = postService.searchPosts(request, pageable);

        // 3. Kiểm tra kết quả (Assert)
        assertNotNull(result, "Kết quả trả về không được phép null");
    }
}