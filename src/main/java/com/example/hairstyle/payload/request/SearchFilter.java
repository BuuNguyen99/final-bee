package com.example.hairstyle.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchFilter {
    private String property;
    private String operator;
    private Object value;
}
