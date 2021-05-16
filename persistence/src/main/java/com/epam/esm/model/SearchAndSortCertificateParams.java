package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class SearchAndSortCertificateParams {
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{0,50}", message = "validate.certificate.search.tagName")
    private String tags;
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{0,50}", message = "validate.certificate.search.name")
    private String name;
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{0,500}", message = "validate.certificate.search.description")
    private String description;
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{0,500}", message = "validate.certificate.search.sort")
    private String sort;
}
