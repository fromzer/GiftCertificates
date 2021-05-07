package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchAndSortCertificateParams implements SearchAndSortParams{
    private String tags;
    private String name;
    private String description;
    private String sort;
}
