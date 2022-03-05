package com.example.hairstyle.service;

import com.example.hairstyle.payload.request.CartProductRequest;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity addItem(CartProductRequest cartProductRequest, String username);

    ResponseEntity removeItem(Long productId, String username);

    ResponseEntity getAll(String username);
}
