package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchOrderByUserIdParams;
import com.epam.esm.model.UserGift;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final GiftTagDao tagDao;
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final OrderService orderService;
    private ModelMapper mapper = new ModelMapper();

    @Autowired
    public UserServiceImpl(UserDao userDao, OrderDao orderDao, GiftTagDao tagDao, OrderService orderService) {
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.tagDao = tagDao;
        this.orderService = orderService;
    }

    @Override
    public UserGift findById(Long id) throws ResourceNotFoundException {
        return mapper.map(Optional.ofNullable(userDao.findById(id))
                        .orElseThrow(ResourceNotFoundException::new),
                UserGift.class);
    }

    @Override
    public List<UserGift> findAll(Pageable pageable) throws ResourceNotFoundException {
        return userDao.findAll(pageable).stream()
                .map(user -> mapper.map(user, UserGift.class))
                .collect(Collectors.toList());
    }


    @Override
    public GiftOrderWithoutCertificatesAndUser findUserOrderInfo(Long orderId, Long userId) throws ResourceNotFoundException {
        return Optional.ofNullable(orderDao.findByUserIdAndOrderId(orderId, userId))
                .map(byId -> mapper.map(byId, GiftOrderWithoutCertificatesAndUser.class))
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<GiftOrder> findUserOrders(Long id, Pageable pageable) {
        Optional.ofNullable(userDao.findById(id))
                .orElseThrow(ResourceNotFoundException::new);
        return orderDao.findOrdersByUserId(new SearchOrderByUserIdParams(id), pageable).stream()
                .map(order -> mapper.map(order, GiftOrder.class))
                .collect(Collectors.toList());
    }

    @Override
    public GiftTag findMostPopularUserTag(Long userId) throws ResourceNotFoundException {
        return Optional.ofNullable(tagDao.findMostPopularUserTag(userId))
                .map(tag -> mapper.map(tag, GiftTag.class))
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Long createUserOrder(Long userId, List<GiftCertificate> giftCertificates) throws CreateResourceException {
        return orderService.createOrder(userId, giftCertificates);
    }
}
