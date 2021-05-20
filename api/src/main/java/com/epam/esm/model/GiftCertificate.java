package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class GiftCertificate extends RepresentationModel<GiftCertificate> {
    private Long id;

    @NotBlank(message = "validate.emptyField")
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{1,50}", message = "validate.certificate.name")
    private String name;

    @NotBlank(message = "validate.emptyField")
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{1,500}", message = "validate.certificate.description")
    private String description;

    @NotNull(message = "validate.emptyField")
    @Range(message = "validate.certificate.price")
    private BigDecimal price;

    @NotNull(message = "validate.emptyField")
    @Range(message = "validate.certificate.duration")
    private Integer duration;
    private ZonedDateTime createDate;
    private ZonedDateTime lastUpdateDate;
    private Set<GiftTag> tags;
}
