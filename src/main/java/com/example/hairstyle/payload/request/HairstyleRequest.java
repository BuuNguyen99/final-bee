package com.example.hairstyle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HairstyleRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    @NotBlank(message = "Hair cut is not null")
    private String hairCut;

    @NotBlank(message = "Style is not null")
    private String style;

    @NotBlank(message = "Length is not null")
    private String hairLength;

    @NotNull
    private Boolean gender;

    @NotNull(message = "Face Shape is not null")
    private Set<Long> faceShapeIds;

    @NotNull(message = "Age range is not null")
    private Set<Long> ageRangeIds;

    private String imageUrl;
}