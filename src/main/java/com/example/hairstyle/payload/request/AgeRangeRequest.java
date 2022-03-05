package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class AgeRangeRequest {
    @NotNull
    private Integer min;

    @NotNull
    private Integer max;

    private String rangeDescription;
}
