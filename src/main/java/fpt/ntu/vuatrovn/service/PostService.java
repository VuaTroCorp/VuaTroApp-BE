package fpt.ntu.vuatrovn.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import fpt.ntu.vuatrovn.dto.CreatePostRequest;
import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.dto.UpdatePostRequest;
import fpt.ntu.vuatrovn.entity.Image;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.RoomType;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.PostStatus;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.repository.TypeRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.specification.PostSpecification;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TypeRepository typeRepository;
    private final SupabaseStorageService supabaseStorageService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       TypeRepository typeRepository,
                    SupabaseStorageService supabaseStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    // ==========================================
    // 1. API TẠO BÀI ĐĂNG
    // ==========================================
    public void createPost(CreatePostRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoomType type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Type not found"));

        if (request.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá không được để trống");
        }

        if (request.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá phải lớn hơn 0");
        }

        // Create post
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setPrice(request.getPrice());
        post.setArea(request.getArea());
        post.setRoom_quantity(request.getRoomQuantity());
        post.setAdrress(request.getAddress());
        post.setDecription(request.getDescription());
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());
        post.setStatus(PostStatus.PENDING);
        post.setUser(user);
        post.setType(type);

        // Save Image
        List<Image> images = new ArrayList<>();
        int index = 0;
    for (MultipartFile file : request.getImages()) {

        try {
            String imageUrl = supabaseStorageService.uploadFile(file);

            Image image = new Image();
            image.setUrl(imageUrl);
            image.setOrder_index(index++);
            image.setPost(post);
            images.add(image);

        } catch (Exception e) {
            throw new RuntimeException("Upload ảnh thất bại");
        }
    }

        post.setImages(images);
        postRepository.save(post);
    }


    // ==========================================
    // 2. API TÌM KIẾM BÀI ĐĂNG 
    // ==========================================
    public Page<Post> searchPosts(PostSearchRequest request, Pageable pageable) {
        // Gọi đến PostSpecification để tạo bộ lọc động
        Specification<Post> spec = PostSpecification.filterPosts(request);
        
        // Trả về kết quả phân trang
        return postRepository.findAll(spec, pageable);
    }


    // ==========================================
    // 3. GET POST DETAIL API
    // ==========================================
    public Post getPostDetail(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // ==========================================
    // 4. EDIT POST API
    // ==========================================
    public void updatePost(Long postId, UpdatePostRequest request, String email) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post không tồn tại"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền sửa bài");
        }

        RoomType type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type không tồn tại"));
        
        // Check the original poster.
        if (!post.getUser().getId().equals(user.getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền sửa bài đăng này");
    }
        // Update post information
        post.setTitle(request.getTitle());
        post.setPrice(request.getPrice());
        post.setArea(request.getArea());
        post.setRoom_quantity(request.getRoomQuantity());
        post.setAdrress(request.getAddress());
        post.setDecription(request.getDescription());
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());
        post.setType(type);

        // Delete Image
        if (request.getDeleteImageIds() != null) {

            List<Image> images = post.getImages();

            images.removeIf(image -> {

                if (request.getDeleteImageIds().contains(image.getId())) {

                    try {
                        supabaseStorageService.deleteFile(image.getUrl());
                    } catch (Exception e) {
                        throw new RuntimeException("Deleting photos from Supabase failed.");
                    }
                    return true;
                }
                return false;
            });
        }

        if (request.getNewImages() != null && !request.getNewImages().isEmpty()) {

            int index = post.getImages().size();

            for (MultipartFile file : request.getNewImages()) {
                try {

                String imageUrl = supabaseStorageService.uploadFile(file);

                Image image = new Image();
                image.setUrl(imageUrl);
                image.setOrder_index(index++);
                image.setPost(post);

                post.getImages().add(image);
                } catch (Exception e) {
                    // TODO: handle exception
                    throw new RuntimeException("Upload Image to Supabase failed");
                }
            }
        }
        postRepository.save(post);
        System.out.println("Post owner: " + post.getUser().getEmail());
        System.out.println("User login: " + email);
    }
}


    