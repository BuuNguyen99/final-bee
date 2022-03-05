package com.example.hairstyle.service.implementation;

import com.example.hairstyle.constant.ResponseText;
import com.example.hairstyle.entity.Role;
import com.example.hairstyle.payload.response.MessageResponse;
import com.example.hairstyle.payload.response.PagingResponse;
import com.example.hairstyle.repository.RoleRepository;
import com.example.hairstyle.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity getAllRoles(int page, int size) {
        var paging  = PageRequest.of(page, size);
        var rolePages = roleRepository.findAll(paging);

        if(rolePages.isEmpty()){
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(true, ResponseText.NO_DATA_RETRIEVAL));
        }

        var resultPage = new PagingResponse<Role>();
        resultPage.setTotalPages(rolePages.getTotalPages());
        resultPage.setCurrentPage(rolePages.getNumber());
        resultPage.setTotalItems(rolePages.getTotalElements());
        resultPage.setContent(rolePages.getContent());

        return ResponseEntity
                .ok()
                .body(resultPage);
    }
}
