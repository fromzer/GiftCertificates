package com.epam.esm.service.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.mapper.CertificateMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private TagRepository tagRepository;

    private OrderServiceImpl orderService;

    private Order order;
    private Certificate certificate;
    private List<Certificate> certificates;
    private User user;

    @BeforeEach
    void setUp() {
        GiftCertificateServiceImpl certificateService = new GiftCertificateServiceImpl(certificateRepository, tagRepository);
        orderService = new OrderServiceImpl(orderRepository, certificateService, userRepository);
        user = User.builder()
                .id(1L)
                .login("alex")
                .firstName("Alex")
                .lastName("Pusher")
                .build();
        certificate = Certificate.builder()
                .id(1L)
                .name("New certificate name")
                .description("New certificate description")
                .price(BigDecimal.valueOf(12.13))
                .duration(1)
                .createDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .lastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .build();
        certificates = new ArrayList<>();
        certificates.add(certificate);
        order = Order.builder()
                .id(1L)
                .user(user)
                .certificates(certificates)
                .cost(certificate.getPrice())
                .purchaseDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .build();
        List<Order> orders = new ArrayList<>();
        orders.add(order);
    }

    @Test
    void createOrder() {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(user));
        when(certificateRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(certificate));
        when(orderRepository.save(any())).thenReturn(order);
        List<GiftCertificate> giftCertificates = certificates.stream()
                .map(CertificateMapper.CERTIFICATE_MAPPER::certificateToGiftCertificate)
                .collect(Collectors.toList());
        assertEquals(order.getId(), orderService.createOrder(user.getId(), giftCertificates).getId());
    }
}