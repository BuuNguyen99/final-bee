package com.example.hairstyle.repository;

import com.example.hairstyle.entity.HairstyleReview;
import com.example.hairstyle.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HairstyleReviewRepository extends JpaRepository<HairstyleReview, Long> {
    Optional<HairstyleReview> findByProfile_IdAndHairstyle_Id(Long profileId, Long hairstyleId);
}
