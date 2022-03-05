package com.example.hairstyle.controller;

import com.example.hairstyle.payload.request.DiscountRequest;
import com.example.hairstyle.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;

@RestController
@RequestMapping("/api/discount")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity getAllDiscount(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return discountService.getAll(page, size);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity createDiscount(@Valid @RequestBody DiscountRequest discountRequest) {
        return discountService.createDiscount(discountRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity updateDiscount(@RequestBody DiscountRequest discountRequest,
                                         @RequestParam String slug) {
        return discountService.updateDiscount(discountRequest, slug);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/multiDelete")
    public ResponseEntity deleteDiscount(@Valid @RequestBody Long[] ids) {
        var idSet = new HashSet<>(Arrays.asList(ids));
        return discountService.deleteDiscount(idSet);
    }
}
