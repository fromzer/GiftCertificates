package com.epam.esm.hateoas;

import com.epam.esm.model.GiftTag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TagResource implements SimpleRepresentationModelAssembler<GiftTag> {

    @Override
    public void addLinks(EntityModel<GiftTag> resource) {
      //  resource.add(linkTo(methodOn(TagController.class).getTagById(Objects.requireNonNull(resource.getContent()).getId())).withSelfRel()); //??
      //  resource.add(linkTo(methodOn(TagController.class).getAll(new Pageable())).withSelfRel());
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GiftTag>> resources) {
//        UriComponentsBuilder componentsBuilder = linkTo(methodOn(TagController.class)
//                .getAll()
//                .toUriComponentsBuilder();
//        componentsBuilder.encode();
//        Link link = Link.of(componentsBuilder.toUriString());
//        resources.add(link.withSelfRel());
    }
}
