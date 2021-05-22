package com.epam.esm.hateoas;

import com.epam.esm.controller.UserController;
import com.epam.esm.model.GiftOrder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftOrderResource implements RepresentationModelAssembler<GiftOrder, EntityModel<GiftOrder>> {
    @Override
    public EntityModel<GiftOrder> toModel(GiftOrder entity) {
        return EntityModel.of(entity, linkTo(methodOn(UserController.class)
                .getUserOrder(entity.getUser().getId(), entity.getId())).withSelfRel());
    }
}
