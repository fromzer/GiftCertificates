package com.epam.esm.service.impl;

import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.LoginExistsException;
import com.epam.esm.exception.PasswordMismatchException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.exception.UserNotFoundException;
import com.epam.esm.mapper.OrderMapper;
import com.epam.esm.mapper.RegisteredUserMapper;
import com.epam.esm.mapper.TagMapper;
import com.epam.esm.mapper.UserMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.RegisteredUser;
import com.epam.esm.model.SecurityUser;
import com.epam.esm.model.UserGift;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.RoleRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {
    private final static String ROLE_VIEWER = "ROLE_VIEWER";
    private final static String ROLE_BUYER = "ROLE_BUYER";
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TagRepository tagRepository;
    private final RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    public UserServiceImpl(OrderService orderService, UserRepository userRepository, OrderRepository orderRepository, TagRepository tagRepository, RoleRepository roleRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.tagRepository = tagRepository;
        this.roleRepository = roleRepository;
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

    @Override
    public UserGift createUser(RegisteredUser registeredUser) {
        checkCredentialsRegisteredUser(registeredUser);
        User user = RegisteredUserMapper.REGISTER_USER_MAPPER.registeredUserToUser(registeredUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        addDefaultRoles(user);
        return UserMapper.USER_MAPPER.userToUserGift(userRepository.save(user));
    }

    private void addDefaultRoles(User user) {
        Role roleViewer = roleRepository.findByName(ROLE_VIEWER);
        Role roleBuyer = roleRepository.findByName(ROLE_BUYER);
        user.setRoles(Stream.of(roleBuyer, roleViewer).collect(Collectors.toList()));
    }

    private void checkCredentialsRegisteredUser(RegisteredUser registeredUser) {
        if (!registeredUser.getPassword().equals(registeredUser.getRepeatPassword())) {
            throw new PasswordMismatchException();
        }
        if (userRepository.existsByLogin(registeredUser.getLogin())) {
            throw new LoginExistsException();
        }
    }

    @Override
    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);
        return SecurityUser.fromUser(user);
    }

    @Override
    public boolean hasId(Long id) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User byLogin = userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);
        return byLogin.getId().equals(id);
    }
}
