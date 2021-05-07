package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchOrderByUserIdParams implements SearchAndSortParams {
    private Long userId;
}
