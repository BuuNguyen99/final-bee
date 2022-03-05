package com.example.hairstyle.repository;

import com.example.hairstyle.entity.AgeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgeRangeRepository extends JpaRepository<AgeRange, Long> {
}
