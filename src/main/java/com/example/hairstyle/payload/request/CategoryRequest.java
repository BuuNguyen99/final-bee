package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CategoryRequest {
    @NotNull(message = "Title is mandatory")
    private String title;

    private String metaTitle;

    private String content;

    @NotNull
    private Long parentId;
}
