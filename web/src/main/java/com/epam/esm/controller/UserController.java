package com.epam.esm.controller;

import com.epam.esm.hateoas.HateoasResourceBuilder;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.GiftOrderWithoutCertificatesAndUser;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.UserGift;
import com.epam.esm.service.UserService;
import com.epam.esm.service.impl.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Rest controller for Users
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {
    private final UserService userService;
    private final HateoasResourceBuilder resourceBuilder;
    private final AuthenticatedUserService authenticatedUserService;

    @Autowired
    public UserController(UserService userService,
                          HateoasResourceBuilder resourceBuilder, AuthenticatedUserService authenticatedUserService) {
        this.userService = userService;
        this.resourceBuilder = resourceBuilder;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Get user by id
     *
     * @param id user id
     * @return user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('VIEWER') and @authenticatedUserService.hasId(#id))")
    public ResponseEntity<EntityModel<UserGift>> getUserById(@PathVariable @Min(value = 1) Long id) {
        return ResponseEntity.ok(resourceBuilder.getUserResource()
                .toModel(userService.findById(id)));
    }

    /**
     * Create user order
     *
     * @param id               user id
     * @param giftCertificates list of certificates
     * @return order id
     */
    @PostMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUYER') and @authenticatedUserService.hasId(#id))")
    public ResponseEntity<GiftOrder> createOrder(@PathVariable @Min(value = 1) Long id,
                                                 @Valid @RequestBody List<GiftCertificate> giftCertificates) {
        return ResponseEntity.ok(userService.createUserOrder(id, giftCertificates));
    }

    /**
     * Get user orders
     *
     * @param id       user id
     * @param pageable pagination
     * @return list of user orders
     */
    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('VIEWER') and @authenticatedUserService.hasId(#id))")
    @SuppressWarnings("unchecked")
    public ResponseEntity<PagedModel<GiftOrder>> getUserOrders(@PathVariable @Min(value = 1) Long id,
                                                               Pageable pageable) {
        Page<GiftOrder> userOrders = userService.findUserOrders(id, pageable);
        return ResponseEntity.ok(resourceBuilder.getPagedResourcesAssembler()
                .toModel(userOrders, resourceBuilder.getGiftOrderResource()));
    }

    /**
     * Get user order
     *
     * @param id      user id
     * @param orderId order id
     * @return user order
     */
    @GetMapping("/{id}/orders/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('VIEWER') and @authenticatedUserService.hasId(#id))")
    public ResponseEntity<GiftOrderWithoutCertificatesAndUser> getUserOrder(@PathVariable @Min(value = 1) Long id,
                                                                            @PathVariable @Min(value = 1) Long orderId) {
        return ResponseEntity.ok(userService.findUserOrderInfo(orderId, id));
    }

    /**
     * Get the most widely used tag of a user with the highest cost of all orders
     *
     * @param id user id
     * @return tag
     */
    @GetMapping("/{id}/tag")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('VIEWER') and @authenticatedUserService.hasId(#id))")
    public ResponseEntity<EntityModel<GiftTag>> getMostPopularUserTag(@PathVariable @Min(value = 1) Long id) {
        return ResponseEntity.ok(resourceBuilder.getGiftTagResource()
                .toModel(userService.findMostPopularUserTag(id)));
    }

    /**
     * Get all users
     *
     * @param pageable pagination
     * @return list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("unchecked")
    public ResponseEntity<PagedModel<UserGift>> getAll(Pageable pageable) {
        Page<UserGift> all = userService.findAll(pageable);
        return ResponseEntity.ok(resourceBuilder.getPagedResourcesAssembler()
                .toModel(all, resourceBuilder.getUserResource()));
    }
}
