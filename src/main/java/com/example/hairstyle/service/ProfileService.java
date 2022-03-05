package com.example.hairstyle.service;

import com.example.hairstyle.payload.request.ProfileRequest;
import com.example.hairstyle.payload.request.SearchQuery;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity updateProfile(ProfileRequest profileRequest,String username);

    ResponseEntity getProfile(String username);

    ResponseEntity getAllProfiles(Long id, int page, int size);
}
