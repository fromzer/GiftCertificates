package com.epam.esm.hateoas;

import com.epam.esm.controller.TagController;
import com.epam.esm.model.GiftTag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftTagResource implements RepresentationModelAssembler<GiftTag, EntityModel<GiftTag>> {
    @Override
    public EntityModel<GiftTag> toModel(GiftTag entity) {
        return EntityModel.of(entity, linkTo(methodOn(TagController.class)
                .getTagById(entity.getId())).withSelfRel());
    }
}
