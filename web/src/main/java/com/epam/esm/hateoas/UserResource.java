package com.epam.esm.hateoas;

import com.epam.esm.controller.UserController;
import com.epam.esm.model.UserGift;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResource implements RepresentationModelAssembler<UserGift, EntityModel<UserGift>> {

    @Override
    public EntityModel<UserGift> toModel(UserGift entity) {
        return EntityModel.of(entity, linkTo(methodOn(UserController.class)
                .getUserById(entity.getId()))
                .withSelfRel());
    }
}
