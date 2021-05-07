package com.epam.esm.hateoas;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
@Builder
public class HateoasResourceBuilder {
    private final CertificateResource certificateResource;
    private final OrderResource orderResource;
    private final TagResource tagResource;
    private final UserResource userResource;

    @Autowired
    public HateoasResourceBuilder(CertificateResource certificateResource, OrderResource orderResource, TagResource tagResource, UserResource userResource) {
        this.certificateResource = certificateResource;
        this.orderResource = orderResource;
        this.tagResource = tagResource;
        this.userResource = userResource;
    }
}
