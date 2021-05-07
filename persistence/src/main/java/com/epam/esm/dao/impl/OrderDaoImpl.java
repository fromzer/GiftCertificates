package com.epam.esm.dao.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.criteria.SearchOrderByUserIdPredicate;
import com.epam.esm.dao.criteria.SearchOrderByUserIdQuery;
import com.epam.esm.model.Pageable;
import com.epam.esm.entity.Order;
import com.epam.esm.model.SearchOrderByUserIdParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class OrderDaoImpl implements OrderDao {
    private static final String SELECT_ORDER_BY_USER_ID_AND_ORDER_ID = "SELECT id, cost, purchase_date, user_id FROM orders WHERE id = :id AND user_id = :userId";
    private static final String ID = "id";
    private static final String USER_ID = "userId";

    private final GiftDaoBean<Order> giftDao;

    @Autowired
    public OrderDaoImpl(GiftDaoBean giftDao) {
        this.giftDao = giftDao;
        giftDao.setClazz(Order.class);
    }

    @Override
    @Transactional
    public Long create(Order order) {
        return giftDao.create(order);
    }

    @Override
    public Order findById(Long id) {
        return giftDao.findById(id);
    }

    @Override
    public Order findByUserIdAndOrderId(Long orderId, Long userId) {
        Map<String, String> params = new HashMap<>();
        params.put(ID, String.valueOf(orderId));
        params.put(USER_ID, String.valueOf(userId));
        return giftDao.createNativeQuery(SELECT_ORDER_BY_USER_ID_AND_ORDER_ID, params);
    }

    @Override
    public List<Order> findAll(Pageable pageable) {
        return giftDao.findAll(pageable);
    }

    @Override
    public List<Order> findOrdersByUserId(SearchOrderByUserIdParams params, Pageable pageable) {
        return giftDao.findEntitiesByParams(params, pageable,
                new SearchOrderByUserIdPredicate(), new SearchOrderByUserIdQuery());
    }
}
