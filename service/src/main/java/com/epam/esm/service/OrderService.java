package com.epam.esm.service;

import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.model.GiftCertificate;

import java.util.List;

/**
 * Base interface for Order Service
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface OrderService {
    /**
     * Create order
     *
     * @param userId           user id
     * @param giftCertificates list of certificates
     * @return order id
     * @throws CreateResourceException if error is occurred during SQL command execution
     */
    Long createOrder(Long userId, List<GiftCertificate> giftCertificates) throws CreateResourceException;
}
