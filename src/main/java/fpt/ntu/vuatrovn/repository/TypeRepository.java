package fpt.ntu.vuatrovn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fpt.ntu.vuatrovn.entity.RoomType;

public interface TypeRepository extends JpaRepository<RoomType,Long>{
    
}
