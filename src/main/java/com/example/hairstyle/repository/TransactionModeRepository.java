package com.example.hairstyle.repository;

import com.example.hairstyle.entity.TransactionMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionModeRepository extends JpaRepository<TransactionMode, Long> {
}
