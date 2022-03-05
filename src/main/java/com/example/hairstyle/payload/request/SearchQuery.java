package com.example.hairstyle.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchQuery {
    private List<SearchFilter> searchFilters;

    private SortOrder sortOrder;

    private List<JoinColumnProps> joinColumnProps;
}
