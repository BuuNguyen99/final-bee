package com.example.hairstyle.service;

import com.example.hairstyle.payload.request.DiscountRequest;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface DiscountService {
    ResponseEntity getAll(int page, int size);
    ResponseEntity createDiscount(DiscountRequest discountRequest);
    ResponseEntity updateDiscount(DiscountRequest discountRequest, String slug);
    ResponseEntity deleteDiscount(Set<Long> ids);
    ResponseEntity softDeleteDiscount(Set<Long> ids);
}
