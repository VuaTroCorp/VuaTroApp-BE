package fpt.ntu.vuatrovn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Bổ sung import
import org.springframework.stereotype.Repository;

import fpt.ntu.vuatrovn.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    
}