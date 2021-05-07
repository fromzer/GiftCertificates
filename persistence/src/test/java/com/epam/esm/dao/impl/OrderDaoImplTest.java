package com.epam.esm.dao.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchOrderByUserIdParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
@Transactional
class OrderDaoImplTest {
    @Autowired
    private OrderDaoImpl orderDao;

    @Autowired
    private GiftCertificateDAOImpl certificateDAO;

    @Autowired
    private UserDaoImpl userDao;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = new Pageable(1, 10);
    }

    @Test
    void shouldCreateOrder() {
        Certificate firstCertificate = certificateDAO.findById(6l);
        Certificate secondCertificate = certificateDAO.findById(7l);
        List<Certificate> certificateList = new ArrayList<>();
        certificateList.add(firstCertificate);
        certificateList.add(secondCertificate);
        User user = userDao.findById(2l);
        BigDecimal cost = firstCertificate.getPrice().add(secondCertificate.getPrice());
        Order order = Order.builder()
                .user(user)
                .certificates(certificateList)
                .cost(cost)
                .purchaseDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .build();
        orderDao.create(order);
        SearchOrderByUserIdParams params = new SearchOrderByUserIdParams(user.getId());
        List<Order> ordersByUserId = orderDao.findOrdersByUserId(params, pageable);
        List<BigDecimal> costsOfOrder = ordersByUserId.stream()
                .map(ord -> order.getCost())
                .collect(Collectors.toList());
        assertTrue(costsOfOrder.contains(order.getCost()));
    }

    @Test
    void shouldFindOrderById() {
        Order excepted = orderDao.findById(8L);
        assertNotNull(excepted);
        assertEquals(excepted.getId(), 8);
    }

    @Test
    void findByUserIdAndOrderId() {
        User user = userDao.findById(2l);
        Order exceptedOrder = orderDao.findById(8l);
        Order actualOrder = orderDao.findByUserIdAndOrderId(exceptedOrder.getId(), user.getId());
        assertEquals(exceptedOrder, actualOrder);
    }

    @Test
    void shouldFindAllOrders() {
        Order actual = orderDao.findById(8L);
        List<Order> expected = orderDao.findAll(pageable);
        assertEquals(expected.get(0), actual);
    }

    @Test
    void findOrdersByUserId() {
        SearchOrderByUserIdParams params = new SearchOrderByUserIdParams(2l);
        List<Order> ordersByUserId = orderDao.findOrdersByUserId(params, pageable);
        Long excepted = ordersByUserId.get(0).getId();
        Long actual = 8l;
        assertEquals(excepted, actual);
    }
}