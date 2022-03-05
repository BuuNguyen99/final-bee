package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
