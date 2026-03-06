package fpt.ntu.vuatrovn.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.server.ResponseStatusException;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fpt.ntu.vuatrovn.dto.CreatePostRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.RoomType;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.repository.TypeRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TypeRepository typeRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost_success() {

        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Phòng trọ");
        request.setPrice(-45f);
        request.setArea(20f);
        request.setRoomQuantity(1);
        request.setAddress("Nha Trang");
        request.setDescription("Phòng sạch");
        request.setLatitude(12.238);
        request.setLongitude(109.196);
        request.setTypeId(1L);
        request.setImageUrls(List.of("img1.jpg", "img2.jpg"));

        User user = new User();
        RoomType type = new RoomType();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(typeRepository.findById(1L))
                .thenReturn(Optional.of(type));

        postService.createPost(request, "test@gmail.com");

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_fail_whenPriceNull() {

        CreatePostRequest request = new CreatePostRequest();
        request.setPrice(null);
        request.setTypeId(1L);

        User user = new User();
        RoomType type = new RoomType();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(typeRepository.findById(1L))
                .thenReturn(Optional.of(type));

        assertThrows(ResponseStatusException.class,
                () -> postService.createPost(request, "test@gmail.com"));

        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_fail_whenPriceInvalid() {

        CreatePostRequest request = new CreatePostRequest();
        request.setPrice(-10f);
        request.setTypeId(1L);

        User user = new User();
        RoomType type = new RoomType();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(typeRepository.findById(1L))
                .thenReturn(Optional.of(type));

        assertThrows(ResponseStatusException.class,
                () -> postService.createPost(request, "test@gmail.com"));

        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_fail_whenUserNotFound() {

        CreatePostRequest request = new CreatePostRequest();
        request.setPrice(100f);
        request.setTypeId(1L);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> postService.createPost(request, "test@gmail.com"));

        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_fail_whenTypeNotFound() {

        CreatePostRequest request = new CreatePostRequest();
        request.setPrice(100f);
        request.setTypeId(1L);

        User user = new User();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(typeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> postService.createPost(request, "test@gmail.com"));

        verify(postRepository, never()).save(any());
    }
}
