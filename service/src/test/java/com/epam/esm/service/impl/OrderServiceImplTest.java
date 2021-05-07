package com.epam.esm.service.impl;

import com.epam.esm.dao.impl.GiftCertificateDAOImpl;
import com.epam.esm.dao.impl.GiftTagDAOImpl;
import com.epam.esm.dao.impl.OrderDaoImpl;
import com.epam.esm.dao.impl.UserDaoImpl;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderDaoImpl orderDao;
    @Mock
    private UserDaoImpl userDao;
    @Mock
    private GiftCertificateDAOImpl certificateDAO;
    @Mock
    private GiftTagDAOImpl tagDAO;

    private OrderServiceImpl orderService;
    private GiftCertificateServiceImpl certificateService;

    private ModelMapper modelMapper;
    private Pageable pageable;
    private List<Order> orders;
    private Order order;
    private Certificate certificate;
    private List<Certificate> certificates;
    private User user;

    @BeforeEach
    void setUp() {
        pageable = new Pageable(1, 10);
        modelMapper = new ModelMapper();
        certificateService = new GiftCertificateServiceImpl(certificateDAO, tagDAO);
        orderService = new OrderServiceImpl(orderDao, certificateService, userDao);
        user = User.builder()
                .id(1l)
                .login("alex")
                .firstName("Alex")
                .lastName("Pusher")
                .build();
        certificate = Certificate.builder()
                .id(1l)
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
                .id(1l)
                .user(user)
                .certificates(certificates)
                .cost(certificate.getPrice())
                .purchaseDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .build();
        orders = new ArrayList<>();
        orders.add(order);
    }

    @Test
    void createOrder() {
        when(userDao.findById(any())).thenReturn(user);
        when(certificateDAO.findById(any())).thenReturn(certificate);
        when(orderDao.create(any())).thenReturn(1l);
        List<GiftCertificate> giftCertificates = certificates.stream()
                .map(cert -> modelMapper.map(cert, GiftCertificate.class))
                .collect(Collectors.toList());
        assertEquals(order.getId(), orderService.createOrder(user.getId(), giftCertificates));
    }
}