package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaSpecificationExecutor<Product>, PagingAndSortingRepository<Product,Long> {
    Optional<Product> findBySlug(String slug);
}
