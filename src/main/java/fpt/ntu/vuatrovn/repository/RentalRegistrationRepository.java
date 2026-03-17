package fpt.ntu.vuatrovn.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.RentalRegistration;
import fpt.ntu.vuatrovn.entity.User;
public interface RentalRegistrationRepository 
        extends JpaRepository<RentalRegistration, Long> {

    Optional<RentalRegistration> findByUserAndPost(User user, Post post);
            
}
