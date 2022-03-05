package com.example.hairstyle.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinColumnProps {
    private String joinColumnName;
    private SearchFilter searchFilter;
}
