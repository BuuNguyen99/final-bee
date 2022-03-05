package com.example.hairstyle.service.implementation;

import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.entity.Discount;
import com.example.hairstyle.payload.request.DiscountRequest;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.payload.response.PagingResponse;
import com.example.hairstyle.repository.DiscountRepository;
import com.example.hairstyle.repository.ProfileRepository;
import com.example.hairstyle.service.DiscountService;
import com.example.hairstyle.util.CodeConfig;
import com.example.hairstyle.util.CodeGeneration;
import com.example.hairstyle.util.DateTimeUtils;
import com.example.hairstyle.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ResponseEntity getAll(int page, int size) {
        var paging = PageRequest.of(page, size);

        var discountPages = discountRepository.findAll(paging);

        if (discountPages.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(false, ResponseText.NO_DATA_RETRIEVAL));
        }

        var resultPage = new PagingResponse<Discount>();
        resultPage.setContent(discountPages.getContent());
        resultPage.setCurrentPage(discountPages.getNumber());
        resultPage.setTotalPages(discountPages.getTotalPages());
        resultPage.setTotalItems(discountPages.getTotalElements());

        return ResponseEntity
                .ok(resultPage);
    }

    @Override
    @Transactional
    public ResponseEntity createDiscount(DiscountRequest discountRequest) {
        var isExisted = discountRepository.existsByName(discountRequest.getName());

        if(isExisted){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_DISCOUNT));
        }

        var codeConfig = new CodeConfig(8,null,null,null,null);
        var code = CodeGeneration.generate(codeConfig);
        var newDiscount = new Discount();

        newDiscount.setCode(code);
        newDiscount.setDiscountAmount(discountRequest.getDiscountAmount());
        newDiscount.setCurrentUse(0);
        newDiscount.setDescription(discountRequest.getDescription());
        newDiscount.setExpiresAt(DateTimeUtils.parseStringToDateTime(discountRequest.getExpiresAt()));
        newDiscount.setStartsAt(DateTimeUtils.parseStringToDateTime(discountRequest.getStartsAt()));
        newDiscount.setName(discountRequest.getName());
        newDiscount.setMaxUse(discountRequest.getMaxUse());
        newDiscount.setSlug(SlugUtils.makeSlug(discountRequest.getName()));

        discountRepository.save(newDiscount);

        return ResponseEntity
                .ok(newDiscount);
    }

    @Override
    @Transactional
    public ResponseEntity updateDiscount(DiscountRequest discountRequest, String slug) {
        var discountOptional = discountRepository.findBySlug(slug);

        if (discountOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NO_DATA_RETRIEVAL));
        }

        var isExisted = discountRepository.existsByName(discountRequest.getName());

        if(isExisted){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.EXISTED_DISCOUNT));
        }

        var discount = discountOptional.get();

        if (StringUtils.hasText(discountRequest.getName())) {
            discount.setName(discountRequest.getName());
            discount.setSlug(SlugUtils.makeSlug(discountRequest.getName()));
        }

        if (Objects.nonNull(discountRequest.getDiscountAmount())) {
            discount.setDiscountAmount(discountRequest.getDiscountAmount());
        }

        if (Objects.nonNull(discountRequest.getCurrentUse())) {
            discount.setCurrentUse(discountRequest.getCurrentUse());
        }

        if (StringUtils.hasText(discountRequest.getDescription())) {
            discount.setDescription(discountRequest.getDescription());
        }

        if (Objects.nonNull(discountRequest.getExpiresAt())) {
            discount.setExpiresAt(DateTimeUtils.parseStringToDateTime(discountRequest.getExpiresAt()));
        }

        if (Objects.nonNull(discountRequest.getStartsAt())) {
            discount.setStartsAt(DateTimeUtils.parseStringToDateTime(discountRequest.getStartsAt()));
        }

        if (Objects.nonNull(discountRequest.getMaxUse())) {
            discount.setMaxUse(discount.getMaxUse());
        }

        discountRepository.save(discount);

        return ResponseEntity
                .ok(discount);
    }

    @Override
    @Transactional
    public ResponseEntity deleteDiscount(Set<Long> ids) {
        var discounts = discountRepository.findAllById(ids);

        if (discounts.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NO_DATA_RETRIEVAL));
        }

        discounts.forEach(discount -> {
            discount.getProfiles().forEach(profile -> {
                profile.removeDiscount(discount);
            });
        });

        discountRepository.deleteAll(discounts);

        return ResponseEntity
                .ok(new MessageResponse(true, ResponseText.DELETE_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ResponseEntity softDeleteDiscount(Set<Long> ids) {
        var discounts = discountRepository.findAllById(ids);

        if (discounts.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NO_DATA_RETRIEVAL));
        }

        discounts.forEach(discount -> discount.setIsDeleted(true));

        discountRepository.saveAll(discounts);

        return ResponseEntity
                .ok(new MessageResponse(true,ResponseText.DELETE_SUCCESSFULLY));
    }
}
