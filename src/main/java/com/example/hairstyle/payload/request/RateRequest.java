package com.example.hairstyle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateRequest {
    @NotNull(message = "Rate is mandatory")
    private Double rating;

    @Lob
    private String content;

    @NotNull(message = "Id is mandatory")
    private Long id;
}
