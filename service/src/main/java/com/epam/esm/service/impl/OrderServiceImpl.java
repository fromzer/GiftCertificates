package com.epam.esm.service.impl;

import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.mapper.OrderMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.UserGift;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final GiftCertificateService certificateService;
    private final UserRepository userRepository;
    private ModelMapper mapper = new ModelMapper();


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, GiftCertificateService certificateService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.certificateService = certificateService;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public GiftOrder createOrder(Long userId, List<GiftCertificate> giftCertificates) throws CreateResourceException {
        return Optional.ofNullable(userRepository.findById(userId))
                .map(user -> buildGiftOrder(giftCertificates, user.get()))
                .map(order -> mapper.map(order, Order.class))
                .map(orderRepository::save)
                .map(OrderMapper.ORDER_MAPPER::orderToGiftOrder)
                .orElseThrow(ResourceNotFoundException::new);
    }

    private GiftOrder buildGiftOrder(List<GiftCertificate> giftCertificates, User user) {
        List<GiftCertificate> certificates = giftCertificates.stream()
                .map(GiftCertificate::getId)
                .map(certificateService::findById)
                .collect(Collectors.toList());
        BigDecimal cost = certificates.stream()
                .map(GiftCertificate::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.valueOf(0));
        return GiftOrder.builder()
                .user(mapper.map(user, UserGift.class))
                .certificates(certificates)
                .cost(cost)
                .build();
    }
}
