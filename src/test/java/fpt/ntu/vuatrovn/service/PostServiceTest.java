package fpt.ntu.vuatrovn.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fpt.ntu.vuatrovn.dto.CreatePostRequest;
import fpt.ntu.vuatrovn.dto.PostSearchRequest;
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

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private PostService postService;

    // ==========================================
    // 1. TEST TÍNH NĂNG TÌM KIẾM (Của bạn)
    // ==========================================
    @Test
    void searchPosts_ShouldReturnPageOfPosts() {
        PostSearchRequest request = new PostSearchRequest();
        request.setKeyword("Nha Trang");
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Post> mockPage = new PageImpl<>(Collections.emptyList());
        when(postRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        Page<Post> result = postService.searchPosts(request, pageable);

        assertNotNull(result, "Kết quả trả về không được phép null");
    }

    // ==========================================
    // 2. TEST TÍNH NĂNG TẠO BÀI ĐĂNG (Của đồng đội)
    // ==========================================
@Test
void createPost_success() throws Exception {

    CreatePostRequest request = new CreatePostRequest();
    request.setTitle("Phòng trọ");
    request.setPrice(45f);
    request.setArea(20f);
    request.setRoomQuantity(1);
    request.setAddress("Nha Trang");
    request.setDescription("Phòng sạch");
    request.setLatitude(12.238);
    request.setLongitude(109.196);
    request.setTypeId(1L);

    MockMultipartFile file =
            new MockMultipartFile("images", "test.jpg",
                    "image/jpeg", "test".getBytes());

    request.setImages(List.of(file));

    User user = new User();
    RoomType type = new RoomType();

    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
    when(typeRepository.findById(1L)).thenReturn(Optional.of(type));

    when(supabaseStorageService.uploadFile(any()))
            .thenReturn("https://img.supabase/test.jpg");

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

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(typeRepository.findById(1L)).thenReturn(Optional.of(type));

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

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(typeRepository.findById(1L)).thenReturn(Optional.of(type));

        assertThrows(ResponseStatusException.class,
                () -> postService.createPost(request, "test@gmail.com"));

        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_fail_whenUserNotFound() {
        CreatePostRequest request = new CreatePostRequest();
        request.setPrice(100f);
        request.setTypeId(1L);

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

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

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(typeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> postService.createPost(request, "test@gmail.com"));

        verify(postRepository, never()).save(any());
    }
}