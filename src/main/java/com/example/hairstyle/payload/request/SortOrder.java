package com.example.hairstyle.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SortOrder {
    private List<String> ascendingOrder;

    private List<String> descendingOrder;
}
