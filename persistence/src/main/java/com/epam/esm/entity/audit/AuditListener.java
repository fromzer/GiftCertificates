package com.epam.esm.entity.audit;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AuditListener {

    @PrePersist
    private void onPrePersist(Object obj) {
        if (obj instanceof Certificate) {
            Certificate certificate = (Certificate) obj;
            certificate.setCreateDate(ZonedDateTime.now(ZoneId.systemDefault()));
            certificate.setLastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()));
        } else if (obj instanceof Order) {
            Order order = (Order) obj;
            order.setPurchaseDate(ZonedDateTime.now(ZoneId.systemDefault()));
        }
    }

    @PreUpdate
    private void onPreUpdate(Object obj) {
        if (obj instanceof Certificate) {
            Certificate certificate = (Certificate) obj;
            certificate.setLastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()));
        }
    }
}
