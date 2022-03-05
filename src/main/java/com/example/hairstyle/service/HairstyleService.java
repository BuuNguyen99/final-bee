package com.example.hairstyle.service;

import com.example.hairstyle.payload.request.*;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface HairstyleService {
    ResponseEntity getAllHairStyle(SearchQuery searchQuery, int page, int size);
    ResponseEntity getDetail(String slug);
    ResponseEntity addHairstyle(HairstyleRequest hairstyleRequest);
    ResponseEntity rateHairstyle(String username, RateRequest request);
    ResponseEntity updateHairstyle(HairstyleRequest hairstyleRequest, Long id);
    ResponseEntity deleteHairstyle(Set<Long> ids);
    ResponseEntity getFaceShapes(SearchQuery searchQuery, int page, int size);
    ResponseEntity getAgeRange();
    ResponseEntity addFaceShape(FaceShapeRequest request);
    ResponseEntity addAgeRange(AgeRangeRequest request);
}
