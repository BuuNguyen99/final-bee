package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    private String metaTitle;

    private String summary;

    private Double discount;

    @NotNull(message = "Price is mandatory")
    private Double price;

    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    private String content;

    @NotNull(message = "Category is mandatory")
    private String category;

    private Set<String> imageUrls;

}
