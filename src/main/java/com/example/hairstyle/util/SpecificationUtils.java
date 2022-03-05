package com.example.hairstyle.util;

import com.example.hairstyle.payload.request.JoinColumnProps;
import com.example.hairstyle.payload.request.SearchFilter;
import com.example.hairstyle.payload.request.SearchQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SpecificationUtils {
    private SpecificationUtils() {
    }

    public static <T> Specification<T> bySearchQuery(SearchQuery searchQuery, Class<T> clazz) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            var joinColumnProps = searchQuery.getJoinColumnProps();

            if (joinColumnProps != null && !joinColumnProps.isEmpty()) {
                for (JoinColumnProps joinColumnProp : joinColumnProps) {
                    addJoinColumnProps(predicates, joinColumnProp, criteriaBuilder, root);
                }
            }

            var searchFilters = searchQuery.getSearchFilters();

            if (searchFilters != null && !searchFilters.isEmpty()) {

                for (final SearchFilter searchFilter : searchFilters) {
                    addPredicates(predicates, searchFilter, criteriaBuilder, root);
                }
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    private static <T> void addJoinColumnProps(List<Predicate> predicates, JoinColumnProps joinColumnProp,
                                               CriteriaBuilder criteriaBuilder, Root<T> root) {

        var searchFilter = joinColumnProp.getSearchFilter();
        var joinParent = root.join(joinColumnProp.getJoinColumnName());

        var property = searchFilter.getProperty();
        var expression = joinParent.get(property);

        addPredicate(predicates, searchFilter, criteriaBuilder, expression);

    }

    private static <T> void addPredicates(List<Predicate> predicates, SearchFilter searchFilter,
                                          CriteriaBuilder criteriaBuilder, Root<T> root) {
        var property = searchFilter.getProperty();
        var expression = root.get(property);

        addPredicate(predicates, searchFilter, criteriaBuilder, expression);

    }

    private static void addPredicate(List<Predicate> predicates, SearchFilter searchFilter,
                                     CriteriaBuilder criteriaBuilder, Path expression) {
        switch (searchFilter.getOperator()) {
            case "=":
                predicates.add(criteriaBuilder.equal(expression, searchFilter.getValue()));
                break;
            case "LIKE":
                predicates.add(criteriaBuilder.like(expression, "%" + searchFilter.getValue() + "%"));
                break;
            case "IN":
                predicates.add(criteriaBuilder.in(expression).value(searchFilter.getValue()));
                break;
            case ">":
                predicates.add(criteriaBuilder.greaterThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case "<":
                predicates.add(criteriaBuilder.lessThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case ">=":
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                break;
            case "<=":
                predicates.add(criteriaBuilder.lessThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                break;
            case "!":
                predicates.add(criteriaBuilder.notEqual(expression, searchFilter.getValue()));
                break;
            case "IsNull":
                predicates.add(criteriaBuilder.isNull(expression));
                break;
            case "NotNull":
                predicates.add(criteriaBuilder.isNotNull(expression));
                break;
            default:
                log.error("Predicate is not matched");
                throw new IllegalArgumentException(searchFilter.getOperator() + " is not a valid predicate");
        }

    }
}
