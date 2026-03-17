package fpt.ntu.vuatrovn.service;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when; 
import java.util.Optional;  
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.repository.RentalRegistrationRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RentalRegistrationServiceTest {

    @Mock
    private RentalRegistrationRepository registrationRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RentalRegistrationService service;

    @Test
    void testRegisterViewRoomSuccess() {

        User user = new User();
        Post post = new Post();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        when(registrationRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.empty());

        service.registerViewRoom(1L, "test@gmail.com");

        verify(registrationRepository, times(1)).save(any());
    }
}
