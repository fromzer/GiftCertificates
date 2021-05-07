package com.epam.esm.hateoas;

import com.epam.esm.controller.UserController;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.UserGift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderResource implements SimpleRepresentationModelAssembler<GiftOrder> {

    private final UserResource userResource;
    private final CertificateResource certificateResource;

    @Autowired
    public OrderResource(UserResource userResource, CertificateResource certificateResource) {
        this.userResource = userResource;
        this.certificateResource = certificateResource;
    }

    @Override
    public void addLinks(EntityModel<GiftOrder> resource) {
        resource.add(linkTo(methodOn(UserController.class)
                .getUserOrder(Objects.requireNonNull(resource.getContent()).getUser().getId(), resource.getContent().getId())).withSelfRel());
        GiftOrder giftOrder = resource.getContent();
        UserGift user = giftOrder.getUser();
        user.add(userResource.toModel(user).getLinks());
        List<GiftCertificate> certificates = giftOrder.getCertificates();
        for (GiftCertificate cert : certificates) {
            cert.add(certificateResource.toModel(cert).getLinks());
        }
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GiftOrder>> resources) {
        long userId = resources.getContent().stream()
                .mapToLong(order -> Objects.requireNonNull(order.getContent()).getUser().getId())
                .findAny()
                .orElse(0);
        UriComponentsBuilder componentsBuilder = linkTo(methodOn(UserController.class)
                .getUserOrders(userId, null))
                .toUriComponentsBuilder();
        componentsBuilder.encode();
        Link link = Link.of(componentsBuilder.toUriString());
        resources.add(link.withSelfRel());
    }
}
