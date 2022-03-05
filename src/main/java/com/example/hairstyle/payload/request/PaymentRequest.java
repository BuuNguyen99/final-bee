package com.example.hairstyle.payload.request;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class PaymentRequest {
    @NotNull(message = "ID service is required")
    private Long idServicePack;
    @NotNull(message = "Amount must be not null")
    private Integer amount;
    private String description;
    private String bankCode;
}
