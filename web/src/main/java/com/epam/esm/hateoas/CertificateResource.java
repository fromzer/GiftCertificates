package com.epam.esm.hateoas;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CertificateResource implements SimpleRepresentationModelAssembler<GiftCertificate> {

    @Override
    public void addLinks(EntityModel<GiftCertificate> resource) {
        resource.add(linkTo(methodOn(CertificateController.class).getCertificateById(Objects.requireNonNull(resource.getContent()).getId())).withSelfRel());
        resource.add(linkTo(methodOn(CertificateController.class).getCertificatesWithParameters(null, new Pageable())).withSelfRel());
        if (resource.getContent().getTags() != null) {
            addTagLinks(resource);
        }
    }

    private void addTagLinks(EntityModel<GiftCertificate> resource) {
        for (final GiftTag tag : resource.getContent().getTags()) {
            Link getTagById = linkTo(methodOn(TagController.class).getTagById(tag.getId())).withSelfRel();
            if (!tag.getLinks().contains(getTagById)) {
                tag.add(getTagById);
            }
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GiftCertificate>> resources) {
        resources.add(linkTo(methodOn(CertificateController.class).getCertificatesWithParameters(null, new Pageable())).withSelfRel());
    }
}
