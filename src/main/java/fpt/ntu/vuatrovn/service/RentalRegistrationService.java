package fpt.ntu.vuatrovn.service;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.RentalRegistration;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.RentalStatus;
import fpt.ntu.vuatrovn.repository.PostRepository;
import fpt.ntu.vuatrovn.repository.RentalRegistrationRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalRegistrationService {

    private final RentalRegistrationRepository registrationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void registerViewRoom(Long postId, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // check đã đăng ký chưa
        registrationRepository
                .findByUserAndPost(user, post)
                .ifPresent(r -> {
                    throw new RuntimeException("You already registered this room");
                });

        RentalRegistration registration = new RentalRegistration();

        registration.setUser(user);
        registration.setPost(post);
        registration.setRegistration_date(LocalDate.now());
        registration.setStatus(RentalStatus.PENDING);

        registrationRepository.save(registration);

        // TODO: notification landlord
    }
    
}
