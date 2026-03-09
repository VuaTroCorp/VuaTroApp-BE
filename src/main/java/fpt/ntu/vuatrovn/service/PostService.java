package fpt.ntu.vuatrovn.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fpt.ntu.vuatrovn.dto.CreatePostRequest;
import fpt.ntu.vuatrovn.entity.Image;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.RoomType;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.PostStatus;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.repository.TypeRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TypeRepository typeRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       TypeRepository typeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
    }

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

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setPrice(request.getPrice());
        post.setArea(request.getArea());
        post.setRoom_quantity(request.getRoomQuantity());

        post.setAdrress(request.getAddress());

        post.setDecription(request.getDescription());

        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());

        post.setStatus(PostStatus.NOT_APPROVE);
        post.setUser(user);
        post.setType(type);

        // lưu ảnh
        List<Image> images = new ArrayList<>();

        int index = 0;
        for (String url : request.getImageUrls()) {

            Image image = new Image();
            image.setUrl(url);
            image.setOrder_index(index++);
            image.setPost(post);

            images.add(image);
        }

        post.setImages(images);

        postRepository.save(post);
    }
}