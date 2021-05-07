package com.epam.esm.service.impl;

import com.epam.esm.dao.impl.GiftCertificateDAOImpl;
import com.epam.esm.dao.impl.GiftTagDAOImpl;
import com.epam.esm.dao.impl.OrderDaoImpl;
import com.epam.esm.dao.impl.UserDaoImpl;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.UserGift;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserServiceImpl userService;
    @Mock
    private GiftTagDAOImpl tagDAO;
    @Mock
    private UserDaoImpl userDao;
    @Mock
    private OrderDaoImpl orderDao;
    @Mock
    private GiftCertificateDAOImpl certificateDAO;
    private GiftCertificateServiceImpl certificateService;
    private OrderServiceImpl orderService;
    private Pageable pageable;
    private ModelMapper modelMapper;
    private User excepted;
    private List<Order> orders;
    private Order order;
    private Certificate certificate;
    private List<Certificate> certificates;
    private User user;

    @BeforeEach
    void setUp() {
        certificateService = new GiftCertificateServiceImpl(certificateDAO, tagDAO);
        orderService = new OrderServiceImpl(orderDao, certificateService, userDao);
        modelMapper = new ModelMapper();
        userService = new UserServiceImpl(userDao, orderDao, tagDAO, orderService);
        pageable = new Pageable(1, 10);
        excepted = User.builder()
                .id(2l)
                .login("alex")
                .firstName("Alex")
                .lastName("Pusher")
                .build();
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
    void shouldFindUserById() throws EntityRetrievalException, ResourceNotFoundException {
        when(userDao.findById(anyLong())).thenReturn(excepted);
        UserGift actual = userService.findById(2l);
        assertEquals(excepted.getLogin(), actual.getLogin());
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

    @Test
    void shouldNotFindUserById() throws EntityRetrievalException, ResourceNotFoundException {
        when(userDao.findById(anyLong())).thenReturn(excepted);
        UserGift actual = userService.findById(1L);
        assertNotEquals(excepted, actual);
    }

    @Test
    void shouldFindAllUsers() throws EntityRetrievalException, ResourceNotFoundException {
        List<User> users = new ArrayList<>();
        users.add(excepted);
        when(userDao.findAll(pageable)).thenReturn(users);
        assertEquals(1, userService.findAll(pageable).size());
    }

    @Test
    void shouldFindUserOrderInfo() {
        when(orderDao.findByUserIdAndOrderId(any(), any())).thenReturn(order);
        GiftOrderWithoutCertificatesAndUser actual = userService.findUserOrderInfo(order.getId(), user.getId());
        assertEquals(order.getCost(), actual.getCost());
    }

    @Test
    void shouldFindUserOrders() {
        when(userDao.findById(any())).thenReturn(user);
        when(orderDao.findOrdersByUserId(any(), any())).thenReturn(orders);
        List<GiftOrder> actual = userService.findUserOrders(user.getId(), pageable);
        assertEquals(orders.get(0).getId(), actual.get(0).getId());
    }
}