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
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TypeRepository typeRepository;
    private final SupabaseStorageService supabaseStorageService;

    // ==========================================
    // 1. CREATE POST
    // ==========================================
    public void createPost(CreatePostRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        RoomType type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room type not found"));

        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The price must be greater than 0.");
        }

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

        List<Image> images = new ArrayList<>();
        int index = 0;

        if (request.getImages() != null) {
            for (MultipartFile file : request.getImages()) {
                try {

                    String imageUrl = supabaseStorageService.uploadFile(file);

                    Image image = new Image();
                    image.setUrl(imageUrl);
                    image.setOrder_index(index++);
                    image.setPost(post);

                    images.add(image);

                } catch (Exception e) {
                    throw new RuntimeException("Upload image failed");
                }
            }
        }

        post.setImages(images);
        postRepository.save(post);
    }

    // ==========================================
    // 2. SEARCH POSTS
    // ==========================================
    public Page<Post> searchPosts(PostSearchRequest request, Pageable pageable) {

        Specification<Post> spec = PostSpecification.filterPosts(request);

        return postRepository.findAll(spec, pageable);
    }

    // ==========================================
    // 3. GET POST DETAIL
    // ==========================================
    public Post getPostDetail(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    // ==========================================
    // 4. UPDATE POST
    // ==========================================
    public void updatePost(Long postId, UpdatePostRequest request, String email) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this post.");
        }

        RoomType type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found"));

        // Update thông tin
        post.setTitle(request.getTitle());
        post.setPrice(request.getPrice());
        post.setArea(request.getArea());
        post.setRoom_quantity(request.getRoomQuantity());
        post.setAdrress(request.getAddress());
        post.setDecription(request.getDescription());
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());
        post.setType(type);

        // Xóa ảnh
        if (request.getDeleteImageIds() != null) {

            post.getImages().removeIf(image -> {

                if (request.getDeleteImageIds().contains(image.getId())) {

                    try {
                        supabaseStorageService.deleteFile(image.getUrl());
                    } catch (Exception e) {
                        throw new RuntimeException("Delete image failed");
                    }

                    return true;
                }

                return false;
            });
        }

        // Add new image
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
                    throw new RuntimeException("Upload image failed");
                }
            }
        }

        postRepository.save(post);
    }

    // ==========================================
    // 5. DELETE POST (SOFT DELETE)
    // ==========================================
    public void deletePost(Long postId, String email) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this post.");
        }

        post.setStatus(PostStatus.DELETED);

        postRepository.save(post);
    }

    // ==========================================
    // 6. GET ALL POSTS API
    // ==========================================
    public List<Post> getAllPosts(){
        return postRepository.findByStatus(PostStatus.APPROVED);
    }

    // ==========================================
    // 7. GET USER'S POSTS API
    // ==========================================
    public List<Post> getMyPosts(Long postId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByUser(user);
    }
}