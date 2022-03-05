package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Hairstyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HairstyleRepository extends JpaSpecificationExecutor<Hairstyle>, PagingAndSortingRepository<Hairstyle, Long> {
    @Override
    Page<Hairstyle> findAll(Pageable pageable);

    @Query("select hairstyle from Hairstyle hairstyle where hairstyle.slug = :slug")
    Optional<Hairstyle> findBySlug(@Param("slug") String slug);

    Boolean existsBySlug(String slug);

    Optional<Hairstyle> findByName(String name);
}
