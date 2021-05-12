package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Builder
public class GiftOrder extends RepresentationModel<GiftOrder> {
    private Long id;
    private BigDecimal cost;
    private ZonedDateTime purchaseDate;
    private UserGift user;
    private List<GiftCertificate> certificates;
}
