package com.example.hairstyle.controller;

import com.example.hairstyle.service.FirebaseFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FirebaseFileService firebaseFileService;
    @PostMapping("/profile/pic")
    public ResponseEntity upload(@RequestParam("file") MultipartFile multipartFile) throws IOException{
        log.info("HIT -/upload | File Name : {}", multipartFile.getOriginalFilename());
        return firebaseFileService.uploadFile(multipartFile);
    }

    @PostMapping("/profile/pic/{fileName}")
    public ResponseEntity download(@PathVariable String fileName) throws Exception {
        log.info("HIT -/download | File Name : {}", fileName);
        return firebaseFileService.downloadFile(fileName);
    }
}
