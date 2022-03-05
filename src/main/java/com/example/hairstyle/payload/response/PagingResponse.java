package com.example.hairstyle.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponse<T> {
    private List<T> content;

    private int currentPage;

    private long totalItems;

    private int totalPages;
}
