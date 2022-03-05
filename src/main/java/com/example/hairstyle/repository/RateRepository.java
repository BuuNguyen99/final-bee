package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rating,Long> {
    Optional<Rating> findByProfile_IdAndProduct_Id(Long profileId, Long productId);
    @Query("select rate from Rating rate join rate.product pro where pro.slug = :slug order by rate.createdAt desc")
    List<Rating> findAllBySlug(@Param("slug")String slug);
}
