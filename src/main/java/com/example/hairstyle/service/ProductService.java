package com.example.hairstyle.service;

import com.example.hairstyle.payload.request.PaymentRequest;
import com.example.hairstyle.payload.request.ProductRequest;
import com.example.hairstyle.payload.request.RateRequest;
import com.example.hairstyle.payload.request.SearchQuery;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Set;

public interface ProductService {
    ResponseEntity getAll(SearchQuery searchQuery, int page, int size);
    ResponseEntity addProduct(ProductRequest productRequest);
    ResponseEntity removeProducts(Set<Long> ids);
    ResponseEntity updateProduct(ProductRequest productRequest, Long id);
    ResponseEntity getDetailProduct(String slug, String username);
    ResponseEntity rateProduct(RateRequest rateRequest, String username);
    ResponseEntity favoriteProduct(String username);
    ResponseEntity createPayment(PaymentRequest paymentRequest) throws IOException;
    ResponseEntity getRating(String slug);
}
