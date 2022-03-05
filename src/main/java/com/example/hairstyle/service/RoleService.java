package com.example.hairstyle.service;

import org.springframework.http.ResponseEntity;

public interface RoleService {
    public ResponseEntity getAllRoles(int page, int size);
}
