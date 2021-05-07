package com.epam.esm.service;

import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.UserGift;

import java.util.List;

/**
 * Base interface for User Service
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface UserService {
    /**
     * Get user by id
     *
     * @param id user id
     * @return user
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    UserGift findById(Long id) throws ResourceNotFoundException;

    /**
     * Get all users
     *
     * @param pageable pagination
     * @return list of users
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    List<UserGift> findAll(Pageable pageable) throws ResourceNotFoundException;

    /**
     * Get user order
     *
     * @param orderId order id
     * @param userId  user id
     * @return cost and timestamp of a purchase
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    GiftOrderWithoutCertificatesAndUser findUserOrderInfo(Long orderId, Long userId) throws ResourceNotFoundException;

    /**
     * Get user orders
     *
     * @param id       user id
     * @param pageable pagination
     * @return list of user orders
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    List<GiftOrder> findUserOrders(Long id, Pageable pageable) throws ResourceNotFoundException;

    /**
     * Get the most popular user tag
     *
     * @param userId user id
     * @return tag
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    GiftTag findMostPopularUserTag(Long userId) throws ResourceNotFoundException;

    /**
     * Create user order
     *
     * @param userId           user id
     * @param giftCertificates list of certificates
     * @return order id
     * @throws CreateResourceException if error is occurred during SQL command execution
     */
    Long createUserOrder(Long userId, List<GiftCertificate> giftCertificates) throws CreateResourceException;
}
