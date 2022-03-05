package com.example.hairstyle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRequest {
    private String fileName;

    private String contentType;

    private String fileDownloadUri;

    private Long fileSize;
}
