package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CartProductRequest {
    @NotNull(message = "Product id is mandatory")
    private Long productId;

    @NotNull(message = "The quantity is mandatory")
    private Integer quantity;
}
