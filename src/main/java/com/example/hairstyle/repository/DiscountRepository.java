package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Override
    Page<Discount> findAll(Pageable pageable);

    boolean existsByName(String name);

    Optional<Discount> findBySlug(String slug);
}
