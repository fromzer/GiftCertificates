package com.epam.esm.controller;

import com.epam.esm.hateoas.CertificateResource;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.ModifiedGiftCertificate;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Rest controller for Certificates
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/certificates", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CertificateController {
    private final GiftCertificateService giftCertificateService;
    private final CertificateResource certificateResource;

    @Autowired
    public CertificateController(
            GiftCertificateService giftCertificateService,
            CertificateResource certificateResource) {
        this.giftCertificateService = giftCertificateService;
        this.certificateResource = certificateResource;
    }

    /**
     * Create certificate
     *
     * @param giftCertificate the certificate
     * @return new certificate's id
     */
    @PostMapping
    @PreAuthorize("hasRole('EDITOR')")
    public ResponseEntity<GiftCertificate> create(@Valid @RequestBody GiftCertificate giftCertificate) {
        return ResponseEntity.ok(giftCertificateService.create(giftCertificate));
    }

    /**
     * Update certificate and optionally create received tags
     *
     * @param modifiedGiftCertificate the certificate and optionally tags
     * @return certificate and tags
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR')")
    public ResponseEntity<GiftCertificate> update(@Valid @RequestBody ModifiedGiftCertificate modifiedGiftCertificate,
                                                  @PathVariable @Min(value = 1) Long id) {
        return ResponseEntity.ok(giftCertificateService.update(modifiedGiftCertificate, id));
    }

    /**
     * Delete certificate
     *
     * @param id the certificate id
     * @return response entity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EDITOR')")
    public ResponseEntity<Void> delete(@PathVariable @Min(value = 1) Long id) {
        giftCertificateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get certificate by id
     *
     * @param id the certificate id
     * @return the certificate
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<EntityModel<GiftCertificate>> getCertificateById(@PathVariable @Min(value = 1) Long id) {
        return ResponseEntity.ok(certificateResource.toModel(giftCertificateService.findById(id)));
    }

    /**
     * Get certificates by parameters
     *
     * @param params   the search and sort params
     * @param pageable the pagination
     * @return list of certificates
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<CollectionModel<EntityModel<GiftCertificate>>> getCertificatesWithParameters(
            @Valid @ModelAttribute SearchAndSortCertificateParams params, Pageable pageable) {
        return ResponseEntity
                .ok(certificateResource.toCollectionModel(
                        giftCertificateService.findCertificateByParams(params, pageable)));
    }
}
