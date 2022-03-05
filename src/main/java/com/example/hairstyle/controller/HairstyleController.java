package com.example.hairstyle.controller;

import com.example.hairstyle.payload.request.*;
import com.example.hairstyle.security.jwt.JwtService;
import com.example.hairstyle.service.HairstyleService;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;

@RestController
@RequestMapping("/api/hairstyle")
@RequiredArgsConstructor
@Slf4j
public class HairstyleController {
    private final HairstyleService hairstyleService;

    private final JwtService jwtService;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/getAll")
    public ResponseEntity getAll(@RequestBody SearchQuery searchQuery,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return hairstyleService.getAllHairStyle(searchQuery, page, size);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/detail")
    public ResponseEntity getDetail(@RequestParam String slug) {
        log.info(slug);
        return hairstyleService.getDetail(slug);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity createNew(@Valid @RequestBody HairstyleRequest hairstyleRequest) {
        return hairstyleService.addHairstyle(hairstyleRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity update(@Valid @RequestBody HairstyleRequest hairstyleRequest, Long id) {
        return hairstyleService.updateHairstyle(hairstyleRequest, id);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/faceShape/get")
    public ResponseEntity getFaceShape(@RequestBody SearchQuery searchQuery,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return hairstyleService.getFaceShapes(searchQuery, page, size);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @DeleteMapping("/faceShape/delete")
    public ResponseEntity delete(@RequestBody Long[] ids) {
        var idSet = new HashSet<>(Arrays.asList(ids));
        return hairstyleService.deleteHairstyle(idSet);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/faceShape/add")
    public ResponseEntity addFaceShape(@Valid @RequestBody FaceShapeRequest request) {
        return hairstyleService.addFaceShape(request);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/ageRange/get")
    public ResponseEntity getAgeRange() {
        return hairstyleService.getAgeRange();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/ageRange/add")
    public ResponseEntity addAgeRange(@Valid @RequestBody AgeRangeRequest request) {
        return hairstyleService.addAgeRange(request);
    }


    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/rating")
    public ResponseEntity rateHairstyle(@Valid @RequestBody RateRequest hairstyleReviewRequest,
                                        HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        String username = jwtService.getUsernameFromJwtToken(token);

        return hairstyleService.rateHairstyle(username, hairstyleReviewRequest);
    }
}
