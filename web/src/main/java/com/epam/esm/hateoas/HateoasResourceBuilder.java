package com.epam.esm.hateoas;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Component;

@Data
@Component
@Builder
public class HateoasResourceBuilder {
    private final CertificateResource certificateResource;
    private final UserResource userResource;
    private final GiftTagResource giftTagResource;
    private final GiftOrderResource giftOrderResource;
    private final PagedResourcesAssembler pagedResourcesAssembler;

    @Autowired
    public HateoasResourceBuilder(CertificateResource certificateResource, UserResource userResource, GiftTagResource giftTagResource, GiftOrderResource giftOrderResource, PagedResourcesAssembler pagedResourcesAssembler) {
        this.certificateResource = certificateResource;
        this.userResource = userResource;
        this.giftTagResource = giftTagResource;
        this.giftOrderResource = giftOrderResource;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }
}
