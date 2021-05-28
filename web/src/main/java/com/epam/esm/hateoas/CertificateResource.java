package com.epam.esm.hateoas;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.model.GiftCertificate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CertificateResource implements RepresentationModelAssembler<GiftCertificate, EntityModel<GiftCertificate>> {
    @Override
    public EntityModel<GiftCertificate> toModel(GiftCertificate entity) {
        return EntityModel.of(entity, linkTo(methodOn(CertificateController.class)
                .getCertificateById(entity.getId()))
                .withSelfRel());
    }
}
