package fpt.ntu.vuatrovn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.specification.PostSpecification;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Tìm kiếm bài đăng với bộ lọc động và phân trang
     */
    public Page<Post> searchPosts(PostSearchRequest request, Pageable pageable) {
        // Tạo bộ lọc từ Specification đã viết
        Specification<Post> spec = PostSpecification.filterPosts(request);
        
        // Gọi Repository để lấy dữ liệu phân trang
        return postRepository.findAll(spec, pageable);
    }
}