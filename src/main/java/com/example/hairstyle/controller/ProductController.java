package com.example.hairstyle.controller;

import com.example.hairstyle.payload.request.PaymentRequest;
import com.example.hairstyle.payload.request.ProductRequest;
import com.example.hairstyle.payload.request.RateRequest;
import com.example.hairstyle.payload.request.SearchQuery;
import com.example.hairstyle.security.jwt.JwtService;
import com.example.hairstyle.service.ProductService;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    private final JwtService jwtService;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/getAll")
    public ResponseEntity getProducts(@RequestBody SearchQuery searchQuery,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        searchQuery.getSortOrder().getDescendingOrder().add("createdAt");
        return productService.getAll(searchQuery, page, size);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity updateProduct(@RequestBody ProductRequest productRequest,
                                        @RequestParam Long id) {
        return productService.updateProduct(productRequest, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/addProduct")
    public ResponseEntity addProduct(@Valid @RequestBody ProductRequest productRequest) {
        return productService.addProduct(productRequest);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/getDetail")
    public ResponseEntity getDetail(@RequestParam String slug, HttpServletRequest httpServletRequest) {
        return productService.getDetailProduct(slug, getUsernameFromToken(httpServletRequest));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/multiDelete")
    public ResponseEntity multiDelete(@Valid @RequestBody Long[] ids) {
        var setIds = new HashSet<>(Arrays.asList(ids));
        return productService.removeProducts(setIds);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/rate")
    public ResponseEntity rate(@Valid @RequestBody RateRequest rateRequest,
                               HttpServletRequest httpServletRequest) {
        return productService.rateProduct(rateRequest, getUsernameFromToken(httpServletRequest));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/favorite")
    public ResponseEntity favorite(HttpServletRequest httpServletRequest) {
        return productService.favoriteProduct(getUsernameFromToken(httpServletRequest));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/createPayment")
    public ResponseEntity createPayment(@Valid @RequestBody PaymentRequest paymentRequest) throws IOException {
        return productService.createPayment(paymentRequest);
    }

    private String getUsernameFromToken(HttpServletRequest request) {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        return jwtService.getUsernameFromJwtToken(token);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/rating/get")
    public ResponseEntity getRating(@RequestParam String slug) {
        return productService.getRating(slug);
    }



}
