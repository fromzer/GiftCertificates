package com.epam.esm.dao;

import com.epam.esm.entity.Order;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchOrderByUserIdParams;

import java.util.List;

/**
 * Base interface class for OrderDAO
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface OrderDao {
    /**
     * Create order in DB
     *
     * @param order an order of business model
     * @return entry id
     */
    Long create(Order order);

    /**
     * Find order by id in DB
     *
     * @param id order id
     * @return order
     */
    Order findById(Long id);

    /**
     * Find order by order id and user id in DB
     *
     * @param orderId order id
     * @param userId  user id
     * @return order
     */
    Order findByUserIdAndOrderId(Long orderId, Long userId);

    /**
     * Find all orders in DB
     *
     * @param pageable pagination
     * @return list of orders
     */
    List<Order> findAll(Pageable pageable);

    /**
     * Find all user orders in DB
     *
     * @param params   user id
     * @param pageable pagination
     * @return list of orders
     */
    List<Order> findOrdersByUserId(SearchOrderByUserIdParams params, Pageable pageable);
}
