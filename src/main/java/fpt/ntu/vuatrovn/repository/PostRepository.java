package fpt.ntu.vuatrovn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Bổ sung import
import org.springframework.stereotype.Repository;

import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.PostStatus;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Page<Post> findByUser(User user, Pageable pageable);
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
}