package com.epam.esm.service.impl;

import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.mapper.OrderMapper;
import com.epam.esm.mapper.TagMapper;
import com.epam.esm.mapper.UserMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.UserGift;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TagRepository tagRepository;


    @Autowired
    public UserServiceImpl(OrderService orderService, UserRepository userRepository, OrderRepository orderRepository, TagRepository tagRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public UserGift findById(Long id) throws ResourceNotFoundException {
        return UserMapper.USER_MAPPER.userToUserGift(userRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public List<UserGift> findAll(Pageable pageable) throws ResourceNotFoundException {
        return userRepository.findAll(pageable).stream()
                .map(UserMapper.USER_MAPPER::userToUserGift)
                .collect(Collectors.toList());
    }


    @Override
    public GiftOrderWithoutCertificatesAndUser findUserOrderInfo(Long orderId, Long userId) throws ResourceNotFoundException {
        return orderRepository.findOrderByIdAndUserId(orderId, userId)
                .map(order -> GiftOrderWithoutCertificatesAndUser.builder()
                        .cost(order.getCost())
                        .purchaseDate(order.getPurchaseDate())
                        .build())
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<GiftOrder> findUserOrders(Long id, Pageable pageable) {
        userRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return orderRepository.findByUserId(id, pageable).stream()
                .map(OrderMapper.ORDER_MAPPER::orderToGiftOrder)
                .collect(Collectors.toList());
    }

    @Override
    public GiftTag findMostPopularUserTag(Long userId) throws ResourceNotFoundException {
        return Optional.ofNullable(tagRepository.getMostPopularUserTag(userId))
                .map(TagMapper.TAG_MAPPER::tagToGiftTag)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public GiftOrder createUserOrder(Long userId, List<GiftCertificate> giftCertificates) throws CreateResourceException {
        return orderService.createOrder(userId, giftCertificates);
    }
}
