package com.epam.esm.repository;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql({"classpath:create_tables.sql"})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class OrderDaoImplTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private Pageable pageable;
    private Tag tag = Tag.builder().name("TestTag").build();
    private Certificate certificate = Certificate.builder()
            .name("New certificate name")
            .description("New certificate description")
            .price(BigDecimal.valueOf(12.13))
            .duration(1)
            .createDate(ZonedDateTime.now(ZoneId.systemDefault()))
            .lastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()))
            .tags(Stream.of(tag).collect(Collectors.toSet()))
            .build();
    private User user = User.builder()
            .login("test")
            .firstName("Test")
            .lastName("Testov")
            .password("test")
            .build();
    private Order order = Order.builder()
            .user(user)
            .certificates(Stream.of(certificate).collect(Collectors.toList()))
            .cost(certificate.getPrice())
            .purchaseDate(ZonedDateTime.now(ZoneId.systemDefault()))
            .build();

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().persist(certificate);
        entityManager.getEntityManager().persist(user);
        entityManager.getEntityManager().persist(order);
        entityManager.flush();
    }

    @Test
    void shouldFindOrderById() {
        Order excepted = orderRepository.findById(1L).get();
        assertNotNull(excepted);
        assertEquals(excepted.getId(), 1L);
    }

    @Test
    void findByUserIdAndOrderId() {
        User user = userRepository.findById(1L).get();
        Order exceptedOrder = orderRepository.findById(1L).get();
        Order actualOrder = orderRepository.findOrderByIdAndUserId(exceptedOrder.getId(), user.getId()).get();
        assertEquals(exceptedOrder, actualOrder);
    }

    @Test
    void shouldFindAllOrders() {
        Order actual = orderRepository.findById(1L).get();
        List<Order> expected = orderRepository.findAll(pageable).getContent();
        assertEquals(expected.get(0), actual);
    }
}