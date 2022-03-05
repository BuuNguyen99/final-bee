package com.example.hairstyle.repository;

import com.example.hairstyle.entity.FaceShape;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaceShapeRepository extends JpaSpecificationExecutor<FaceShape>, PagingAndSortingRepository<FaceShape, Long> {
    Page<FaceShape> findAll(Pageable pageable);
}
