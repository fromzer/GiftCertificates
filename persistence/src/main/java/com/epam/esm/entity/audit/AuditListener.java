package com.epam.esm.entity.audit;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AuditListener {
    private final static String ZONE = "+03:00";

    @PrePersist
    private void onPrePersist(Object obj) {
        if (obj instanceof Certificate) {
            Certificate certificate = (Certificate) obj;
            certificate.setCreateDate(ZonedDateTime.now(ZoneId.of(ZONE)));
            certificate.setLastUpdateDate(ZonedDateTime.now(ZoneId.of(ZONE)));
        } else if (obj instanceof Order) {
            Order order = (Order) obj;
            order.setPurchaseDate(ZonedDateTime.now(ZoneId.of(ZONE)));
        }
    }

    @PreUpdate
    private void onPreUpdate(Object obj) {
        if (obj instanceof Certificate) {
            Certificate certificate = (Certificate) obj;
            certificate.setLastUpdateDate(ZonedDateTime.now(ZoneId.of(ZONE)));
        }
    }
}
