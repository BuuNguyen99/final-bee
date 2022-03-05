package com.example.hairstyle.service.implementation;

import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.entity.FaceShape;
import com.example.hairstyle.entity.Hairstyle;
import com.example.hairstyle.entity.HairstyleReview;
import com.example.hairstyle.entity.Rating;
import com.example.hairstyle.payload.request.*;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.payload.response.PagingResponse;
import com.example.hairstyle.repository.*;
import com.example.hairstyle.service.HairstyleService;
import com.example.hairstyle.util.CalculateUtils;
import com.example.hairstyle.util.PagingUtils;
import com.example.hairstyle.util.SlugUtils;
import com.example.hairstyle.util.SpecificationUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class HairstyleServiceImpl implements HairstyleService {
    private final HairstyleRepository hairstyleRepository;

    private final AgeRangeRepository ageRangeRepository;

    private final FaceShapeRepository faceShapeRepository;

    private final HairstyleReviewRepository hairstyleReviewRepository;

    private final ProfileRepository profileRepository;

    @Override
    public ResponseEntity getAllHairStyle(SearchQuery searchQuery, int page, int size) {
        Page<Hairstyle> hairstylePages;

        if (Objects.nonNull(searchQuery)) {
            var paging = PagingUtils.getPageRequest(searchQuery, page, size);
            var spec = SpecificationUtils.bySearchQuery(searchQuery,Hairstyle.class);
            hairstylePages = hairstyleRepository.findAll(spec, paging);
        } else {
            var paging = PageRequest.of(page, size);
            hairstylePages = hairstyleRepository.findAll(paging);
        }

        if (hairstylePages.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new ArrayList<>());
        }

        var resultList = new PagingResponse<Hairstyle>();
        resultList.setContent(hairstylePages.getContent());
        resultList.setCurrentPage(hairstylePages.getNumber());
        resultList.setTotalItems(hairstylePages.getTotalElements());
        resultList.setTotalPages(hairstylePages.getTotalPages());

        return ResponseEntity
                .ok(resultList);
    }

    @Override
    public ResponseEntity getDetail(String slug) {
        var hairstyle = hairstyleRepository.findBySlug(slug);

        if (hairstyle.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_HAIRSTYLE));
        }

        return ResponseEntity
                .ok()
                .body(hairstyle.get());
    }

    @Override
    @Transactional
    public ResponseEntity addHairstyle(HairstyleRequest request) {
        var ageRanges = ageRangeRepository.findAllById(request.getAgeRangeIds());
        if (ageRanges.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_AGE_RANGE));
        }

        var faceShapes = faceShapeRepository.findAllById(request.getFaceShapeIds());
        if (!faceShapes.iterator().hasNext()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_FACE_SHAPE));
        }

        if (hairstyleRepository
                .findByName(request.getName()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_HAIRSTYLE));
        }

        var hairstyle = new Hairstyle();
        hairstyle.setName(request.getName());
        hairstyle.setSlug(SlugUtils.makeSlug(request.getName()));

        if (StringUtils.hasText(request.getDescription())) {
            hairstyle.setDescription(request.getDescription());
        }

        if(StringUtils.hasText(request.getImageUrl())){
            hairstyle.setImageUrl(request.getImageUrl());
        }

        hairstyle.setHairCut(request.getHairCut());
        hairstyle.setAverageRating(5d);
        hairstyle.setGender(request.getGender());
        hairstyle.setStyle(request.getStyle());
        hairstyle.setHairLength(request.getHairLength());
        hairstyle.getAgeRanges().addAll(ageRanges);
        hairstyle.getFaceShapes().addAll(Lists.newArrayList(faceShapes));

        hairstyleRepository.save(hairstyle);

        return ResponseEntity
                .ok()
                .body(hairstyle);
    }

    @Override
    @Transactional
    public ResponseEntity updateHairstyle(HairstyleRequest request, Long id) {
        var hairstyleOptional = hairstyleRepository.findById(id);
        if(hairstyleOptional.isEmpty()){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_HAIRSTYLE));
        }

        var hairstyle = hairstyleOptional.get();

        hairstyle.setName(request.getName());
        hairstyle.setSlug(SlugUtils.makeSlug(request.getName()));

        var ageRanges = ageRangeRepository.findAllById(request.getAgeRangeIds());
        if (ageRanges.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_AGE_RANGE));
            }
        hairstyle.getAgeRanges().addAll(ageRanges);

        var faceShapes = faceShapeRepository.findAllById(request.getFaceShapeIds());
        if (!faceShapes.iterator().hasNext()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_FACE_SHAPE));
        }
        hairstyle.getFaceShapes().addAll(Lists.newArrayList(faceShapes));
        hairstyle.setDescription(request.getDescription());
        hairstyle.setGender(request.getGender());
        hairstyle.setHairCut(request.getHairCut());
        hairstyle.setHairCut(request.getStyle());
        hairstyle.setHairLength(request.getHairLength());
        hairstyle.setImageUrl(request.getImageUrl());

        hairstyleRepository.save(hairstyle);

        return ResponseEntity
                .ok()
                .body(hairstyle);
    }

    @Override
    @Transactional
    public ResponseEntity deleteHairstyle(Set<Long> ids) {
        var hairstyles = hairstyleRepository.findAllById(ids);
        if(!hairstyles.iterator().hasNext()){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_HAIRSTYLE));
        }

        hairstyles.forEach(hairstyle -> {
            hairstyle.getFaceShapes().clear();
            hairstyle.getAgeRanges().clear();
        });

        hairstyleRepository.deleteAll(hairstyles);
        return ResponseEntity
                .ok(new MessageResponse(false, ResponseText.DELETE_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ResponseEntity rateHairstyle(String username, RateRequest request) {
        var hairstyleOptional = hairstyleRepository.findById(request.getId());
        if (hairstyleOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_HAIRSTYLE));
        }

        var profileOptional = profileRepository.findByAccount_Username(username);

        if (profileOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.UNFOUNDED_PROFILE));
        }

        var profile = profileOptional.get();
        var hairstyle = hairstyleOptional.get();

        var rateOptional = hairstyleReviewRepository.findByProfile_IdAndHairstyle_Id(profile.getId(), hairstyle.getId());
        double average;
        if (rateOptional.isPresent()) {
            var rate = rateOptional.get();
            average = CalculateUtils.replaceRate(hairstyle.getHairstyleReviews().size(),
                    hairstyle.getAverageRating(),
                    rate.getRating(),
                    request.getRating());

            rate.setRating(request.getRating());

            hairstyle.setAverageRating(average);
            rate.setRating(request.getRating());
            if (StringUtils.hasText(request.getContent())) {
                rate.setContent(request.getContent());
            }

            hairstyleReviewRepository.save(rate);

            return ResponseEntity
                    .ok(rate);

        } else {
            average = CalculateUtils.addRate(hairstyle.getHairstyleReviews().size(),
                    hairstyle.getAverageRating(),
                    request.getRating());
            hairstyle.setAverageRating(average);

            var newRate = new HairstyleReview();
            newRate.setRating(request.getRating());
            newRate.setPublished(true);
            newRate.setContent(request.getContent());
            newRate.addHairstyle(hairstyle);
            newRate.addProfile(profile);

            hairstyleReviewRepository.save(newRate);

            return ResponseEntity
                    .ok(newRate);
        }
    }

    @Override
    public ResponseEntity getFaceShapes(SearchQuery searchQuery, int page, int size){
        Page<FaceShape> shapes;

        if (Objects.nonNull(searchQuery)) {
            var paging = PagingUtils.getPageRequest(searchQuery, page, size);
            var spec = SpecificationUtils.bySearchQuery(searchQuery,FaceShape.class);
            shapes = faceShapeRepository.findAll(spec, paging);
        } else {
            var paging = PageRequest.of(page, size);
            shapes = faceShapeRepository.findAll(paging);
        }

        if (shapes.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new ArrayList<>());
        }

        var resultList = new PagingResponse<FaceShape>();
        resultList.setContent(shapes.getContent());
        resultList.setCurrentPage(shapes.getNumber());
        resultList.setTotalItems(shapes.getTotalElements());
        resultList.setTotalPages(shapes.getTotalPages());

        return ResponseEntity
                .ok(resultList);
    }

    @Override
    public ResponseEntity getAgeRange(){
        var ageRanges = ageRangeRepository.findAll();
        return ResponseEntity
                .ok(ageRanges);
    }

    @Override
    @Transactional
    public ResponseEntity addFaceShape(FaceShapeRequest request){
        var faceShape = new FaceShape();
        faceShape.setName(request.getName());
        faceShape.setDescription(request.getDescription());

        return ResponseEntity
                .ok(faceShape);
    }

    @Override
    @Transactional
    public ResponseEntity addAgeRange(AgeRangeRequest request){
        var ageRange = new AgeRangeRequest();
        ageRange.setMin(request.getMin());
        ageRange.setMax(request.getMax());
        ageRange.setRangeDescription(request.getRangeDescription());

        return ResponseEntity
                .ok(ageRange);
    }
}