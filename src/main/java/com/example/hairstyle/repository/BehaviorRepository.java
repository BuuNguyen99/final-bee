package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Behavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BehaviorRepository extends JpaRepository<Behavior,Long> {
}
