package com.example.hairstyle.controller;

import com.example.hairstyle.payload.request.CartProductRequest;
import com.example.hairstyle.security.jwt.JwtService;
import com.example.hairstyle.service.CartService;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    private final JwtService jwtService;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/addItem")
    public ResponseEntity addItem(@Valid @RequestBody CartProductRequest cartProductRequest,
                                  HttpServletRequest request) {
        return cartService.addItem(cartProductRequest, getUsernameFromToken(request));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @DeleteMapping("/removeItem")
    public ResponseEntity removeItem(@RequestParam Long productId,
                                     HttpServletRequest request) {
        return cartService.removeItem(productId, getUsernameFromToken(request));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity getAll(HttpServletRequest request){
        return cartService.getAll(getUsernameFromToken(request));
    }

    private String getUsernameFromToken(HttpServletRequest request) {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        return jwtService.getUsernameFromJwtToken(token);
    }

}
