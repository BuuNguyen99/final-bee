package com.example.hairstyle.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
public class DiscountRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    private Integer currentUse;

    @NotNull(message = "Max usage use is mandatory")
    private Integer maxUse;

    @NotNull(message = "Discount amount is mandatory")
    private Integer discountAmount;

    @NotNull(message = "Start date is mandatory")
    private String startsAt;

    @NotNull(message = "Expired date is mandatory")
    private String expiresAt;
}
