package com.epam.esm.hateoas;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.model.GiftCertificate;
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
public class CertificateResource implements SimpleRepresentationModelAssembler<GiftCertificate> {
    private static final String GET_CERTIFICATES = "get_certificates";

    @Override
    public void addLinks(EntityModel<GiftCertificate> resource) {
        resource.add(linkTo(methodOn(CertificateController.class).getCertificateById(Objects.requireNonNull(resource.getContent()).getId())).withSelfRel());
    }


    @Override
    public void addLinks(CollectionModel<EntityModel<GiftCertificate>> resources) {
        UriComponentsBuilder componentsBuilder = linkTo(methodOn(CertificateController.class)
                .getCertificatesWithParameters(null, null))
                .toUriComponentsBuilder()
                .replaceQuery("{?tags,name,description,sort,page,size}");
        componentsBuilder.encode();
        Link link = Link.of(componentsBuilder.toUriString());
        resources.add(link.withRel(GET_CERTIFICATES));
    }
}
