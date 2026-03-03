package fpt.ntu.vuatrovn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fpt.ntu.vuatrovn.entity.Post;

public interface PostRepository extends JpaRepository<Post,Long>{
    
}
