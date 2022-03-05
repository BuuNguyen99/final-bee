package com.example.hairstyle.controller;

import com.example.hairstyle.payload.request.ProfileRequest;
import com.example.hairstyle.payload.request.SearchQuery;
import com.example.hairstyle.security.jwt.JwtService;
import com.example.hairstyle.service.ProfileService;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    private final JwtService jwtService;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/get")
    public ResponseEntity getProfile(HttpServletRequest request) {
        return profileService.getProfile(getUsernameFromToken(request));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getDetail")
    public ResponseEntity getDetailProfile(@RequestParam String username) {
        return profileService.getProfile(username);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity updateProfile(@RequestBody ProfileRequest profileRequest, HttpServletRequest request) {
        return profileService.updateProfile(profileRequest, getUsernameFromToken(request));
    }

    private String getUsernameFromToken(HttpServletRequest request) {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        return jwtService.getUsernameFromJwtToken(token);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/getAll")
    public ResponseEntity getAllProfiles(@RequestParam Long roleId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return profileService.getAllProfiles(roleId, page, size);
    }

}
