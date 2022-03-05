package com.example.hairstyle.service.implementation;

import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.entity.Profile;
import com.example.hairstyle.payload.request.ProfileRequest;
import com.example.hairstyle.payload.request.SearchQuery;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.payload.response.PagingResponse;
import com.example.hairstyle.repository.AccountRepository;
import com.example.hairstyle.repository.ProfileRepository;
import com.example.hairstyle.service.ProfileService;
import com.example.hairstyle.util.PagingUtils;
import com.example.hairstyle.util.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    private final AccountRepository accountRepository;


    @Transactional
    public ResponseEntity updateProfile(ProfileRequest profileRequest, String username) {
        var profileOptional = profileRepository.findByAccount_Username(username);

        if (profileOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_ACCOUNT_ERROR));
        }

        var profile = profileOptional.get();

        if (StringUtils.hasText(profileRequest.getFirstname())) {
            profile.setFirstname(profileRequest.getFirstname());
        }

        if (StringUtils.hasText(profileRequest.getLastname())) {
            profile.setLastname(profileRequest.getLastname());
        }

        if (profileRequest.getGender() != null) {
            profile.setGender(profileRequest.getGender());
        }

        if (StringUtils.hasText(profileRequest.getMobile())) {
            profile.setMobile(profileRequest.getMobile());
        }

        if (StringUtils.hasText(profileRequest.getAddress())) {
            profile.setAddress(profileRequest.getAddress());
        }

        if (Objects.nonNull(profileRequest.getBirthday())) {
            profile.setBirthday(new Date(profileRequest.getBirthday().getTime()));
        }

        if (StringUtils.hasText(profileRequest.getEmail())) {
            var account = accountRepository.findByEmail(profileRequest.getEmail()).get();
            account.setEmail(profileRequest.getEmail());
            accountRepository.save(account);
        }

        profileRepository.save(profile);

        return ResponseEntity.ok().body(profile);
    }

    @Override
    public ResponseEntity getProfile(String username) {
        var profile = profileRepository.findByAccount_Username(username);

        if (profile.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(false, ResponseText.NONEXISTENT_USER_ERROR));
        }

        return ResponseEntity.ok().body(profile.get());
    }

    @Override
    public ResponseEntity getAllProfiles(Long roleId, int page, int size) {
        Page<Profile> profilePages;

        var paging  = PageRequest.of(page, size);

        profilePages = profileRepository.findByRole(roleId, paging);

        if (profilePages.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<Profile>());
        }

        var resultList = new PagingResponse<Profile>();
        resultList.setContent(profilePages.getContent());
        resultList.setCurrentPage(profilePages.getNumber());
        resultList.setTotalItems(profilePages.getTotalElements());
        resultList.setTotalPages(profilePages.getTotalPages());

        return ResponseEntity
                .ok()
                .body(resultList);
    }



}
