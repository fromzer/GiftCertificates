package com.epam.esm.service.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Role;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.mapper.CertificateMapper;
import com.epam.esm.mapper.RegisteredUserMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.RegisteredUser;
import com.epam.esm.model.UserGift;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.RoleRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserServiceImpl userService;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private Pageable pageable;
    private OrderServiceImpl orderService;
    private User excepted;
    private List<Order> orders;
    private Order order;
    private Certificate certificate;
    private List<Certificate> certificates;
    private User user;
    private User createAndFindUser;
    private RegisteredUser registeredUser;
    private Role roleViewer;
    private Role roleBuyer;
    private List<Role> roles = new ArrayList<>();

    @BeforeEach
    void setUp() {
        registeredUser = RegisteredUser.builder()
                .login("test")
                .firstName("Test")
                .lastName("Testov")
                .password("test")
                .repeatPassword("test")
                .build();
        createAndFindUser = RegisteredUserMapper.REGISTER_USER_MAPPER.registeredUserToUser(registeredUser);
        roleViewer = new Role(1L, "ROLE_VIEWER");
        roleBuyer = new Role(2L, "ROLE_BUYER");
        roles.add(roleBuyer);
        roles.add(roleViewer);
        createAndFindUser.setId(1L);
        createAndFindUser.setRoles(roles);
        GiftCertificateServiceImpl certificateService = new GiftCertificateServiceImpl(certificateRepository, tagRepository);
        orderService = new OrderServiceImpl(orderRepository, certificateService, userRepository);
        userService = new UserServiceImpl(orderService, userRepository, orderRepository, tagRepository, roleRepository);
        excepted = User.builder()
                .id(2L)
                .login("alex")
                .firstName("Alex")
                .lastName("Pusher")
                .build();
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
        orders = new ArrayList<>();
        orders.add(order);
    }

    @Test
    void shouldFindUserById() throws EntityRetrievalException, ResourceNotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(excepted));
        UserGift actual = userService.findById(2L);
        assertEquals(excepted.getLogin(), actual.getLogin());
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

    @Test
    void shouldNotFindUserByIdException() throws EntityRetrievalException, ResourceNotFoundException {
        when(userRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void shouldFindAllUsers() throws EntityRetrievalException, ResourceNotFoundException {
        List<User> users = new ArrayList<>();
        users.add(excepted);
        Page<User> userPage = new PageImpl<>(users);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        assertEquals(1, userService.findAll(pageable).size());
    }

    @Test
    void shouldFindUserOrderInfo() {
        when(orderRepository.findOrderByIdAndUserId(anyLong(), anyLong())).thenReturn(java.util.Optional.ofNullable(order));
        GiftOrderWithoutCertificatesAndUser actual = userService.findUserOrderInfo(order.getId(), user.getId());
        assertEquals(order.getCost(), actual.getCost());
    }

    @Test
    void shouldFindUserOrders() {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(user));
        when(orderRepository.findByUserId(any(), any())).thenReturn(orders);
        List<GiftOrder> actual = userService.findUserOrders(user.getId(), null);
        assertEquals(orders.get(0).getId(), actual.get(0).getId());
    }

    @Test
    void createUserOrder() {
        when(userRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(user));
        when(certificateRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(certificate));
        when(orderRepository.save(any())).thenReturn(order);
        List<GiftCertificate> giftCertificates = certificates.stream()
                .map(CertificateMapper.CERTIFICATE_MAPPER::certificateToGiftCertificate)
                .collect(Collectors.toList());
        assertEquals(order.getId(), userService.createUserOrder(user.getId(), giftCertificates).getId());
    }

    @Test
    void createUser() {
        when(roleRepository.findByName("ROLE_VIEWER")).thenReturn(roleViewer);
        when(roleRepository.findByName("ROLE_BUYER")).thenReturn(roleBuyer);
        when(userRepository.save(any())).thenReturn(createAndFindUser);
        UserGift userGift = userService.createUser(registeredUser);
        assertEquals(userGift.getLogin(), registeredUser.getLogin());
    }

    @Test
    void shouldFindUserByLogin() {
        when(userRepository.findByLogin(createAndFindUser.getLogin())).thenReturn(java.util.Optional.ofNullable(createAndFindUser));
        User actual = userService.findUserByLogin(createAndFindUser.getLogin());
        assertEquals(createAndFindUser.getLogin(), actual.getLogin());
    }
    @Test
    void shouldNotFindUserByLoginException() {
        when(userRepository.findByLogin(createAndFindUser.getLogin())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserByLogin(createAndFindUser.getLogin()));
    }

    @Test
    void shouldLoadUserByUsername() {
        when(userRepository.findByLogin(createAndFindUser.getLogin())).thenReturn(java.util.Optional.ofNullable(createAndFindUser));
        assertNotNull(userService.loadUserByUsername(createAndFindUser.getLogin()));
    }

    @Test
    void shouldFindMostPopularUserTag() {
        Tag tag = Tag.builder().id(1L).name("Test").build();
        when(tagRepository.getMostPopularUserTag(createAndFindUser.getId())).thenReturn(tag);
        assertNotNull(userService.findMostPopularUserTag(createAndFindUser.getId()));
    }
}