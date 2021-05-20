package com.epam.esm.hateoas;

import com.epam.esm.controller.UserController;
import com.epam.esm.model.UserGift;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResource implements SimpleRepresentationModelAssembler<UserGift> {
    private static final String GET_ORDERS = "get_user_orders";
    private static final String MAKE_ORDERS = "make_order";
    private static final String GET_USERS = "get_users";

    @Override
    public void addLinks(EntityModel<UserGift> resource) {
        resource.add(linkTo(methodOn(UserController.class).getUserById(resource.getContent().getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getUserOrders(resource.getContent().getId(), null)).withRel(GET_ORDERS));
        resource.add(linkTo(methodOn(UserController.class).createOrder(resource.getContent().getId(), null)).withRel(MAKE_ORDERS));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<UserGift>> resources) {
        UriComponentsBuilder componentsBuilder = linkTo(methodOn(UserController.class)
                .getAll(null))
                .toUriComponentsBuilder()
                .replaceQuery("{?page,size}");
        componentsBuilder.encode();
        Link link = Link.of(componentsBuilder.toUriString());
        resources.add(link.withRel(GET_USERS));
    }
}
