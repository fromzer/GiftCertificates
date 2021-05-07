package com.epam.esm.hateoas;

import com.epam.esm.controller.UserController;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.UserGift;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResource implements SimpleRepresentationModelAssembler<UserGift> {

    @Override
    public void addLinks(EntityModel<UserGift> resource) {
        resource.add(linkTo(methodOn(UserController.class).getUserById(Objects.requireNonNull(resource.getContent()).getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getAll(null)).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class)
                .getMostPopularUserTag(resource.getContent().getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class)
                .getUserOrders(resource.getContent().getId(), new Pageable())).withSelfRel());
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<UserGift>> resources) {
        UriComponentsBuilder componentsBuilder = linkTo(methodOn(UserController.class)
                .getAll(new Pageable()))
                .toUriComponentsBuilder();
        componentsBuilder.encode();
        Link link = Link.of(componentsBuilder.toUriString());
        resources.add(link.withSelfRel());
    }
}
