package fpt.ntu.vuatrovn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Bổ sung import
import org.springframework.stereotype.Repository;

import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.PostStatus;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    List<Post> findByUser(User user);
    List<Post> findByStatus(PostStatus status);
}