package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class FaceShapeRequest {
    @NotNull
    private String name;

    @NotNull
    private String description;
}
