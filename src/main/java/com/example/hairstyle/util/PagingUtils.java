package com.example.hairstyle.util;

import com.example.hairstyle.payload.request.SearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PagingUtils {
    private PagingUtils(){
    }

    public static PageRequest getPageRequest(SearchQuery searchQuery, int pageNumber, int pageSize){
        List<Sort.Order> orders = new ArrayList<>();

        List<String> ascProps = searchQuery.getSortOrder().getAscendingOrder();

        if (ascProps != null && !ascProps.isEmpty()) {
            for (String prop : ascProps) {
                orders.add(Sort.Order.asc(prop));
            }
        }

        List<String> descProps = searchQuery.getSortOrder().getDescendingOrder();

        if (descProps != null && !descProps.isEmpty()) {
            for (String prop : descProps) {
                orders.add(Sort.Order.desc(prop));
            }

        }
        var sort = Sort.by(orders);

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
