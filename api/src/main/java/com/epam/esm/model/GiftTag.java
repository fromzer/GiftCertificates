package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftTag extends RepresentationModel<GiftTag> {
    private Long id;

    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{1,50}", message = "validate.tagName")
    private String name;
}
