package com.example.hairstyle.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseFileService {
    ResponseEntity uploadFile(MultipartFile multipartFile) throws IOException;

    ResponseEntity downloadFile(String fileName) throws Exception;

}
