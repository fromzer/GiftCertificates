package com.epam.esm.service.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.DeleteResourceException;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.ExistEntityException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.exception.UpdateResourceException;
import com.epam.esm.mapper.CertificateMapper;
import com.epam.esm.mapper.ModifiedCertificateMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.ModifiedGiftCertificate;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.utils.GiftServiceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateServiceImpl.class);
    private final static String ZONE = "+03:00";

    private final CertificateRepository certificateRepository;
    private final TagRepository tagRepository;

    @Autowired
    public GiftCertificateServiceImpl(CertificateRepository certificateRepository, TagRepository tagRepository) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public GiftCertificate update(ModifiedGiftCertificate modifiedGiftCertificate, Long id) throws UpdateResourceException {
        GiftCertificate giftCertificate = ModifiedCertificateMapper.MODIFIED_CERTIFICATE_MAPPER.modifiedGiftCertificateToGiftCertificate(modifiedGiftCertificate);
        searchExistingTag(giftCertificate);
        GiftCertificate existing = Optional.ofNullable(findById(id))
                .map(certificate -> {
                    Set<GiftTag> tags = certificate.getTags();
                    GiftServiceUtils.copyNonNullProperties(modifiedGiftCertificate, certificate);
                    certificate.setLastUpdateDate(ZonedDateTime.now(ZoneId.of(ZONE)));
                    Optional.ofNullable(tags)
                            .map(previousTags -> certificate.getTags().addAll(previousTags));
                    return certificate;
                })
                .orElseThrow(ResourceNotFoundException::new);
        Certificate modified = certificateRepository.save(CertificateMapper.CERTIFICATE_MAPPER
                .giftCertificateToCertificate(existing));
        return CertificateMapper.CERTIFICATE_MAPPER.certificateToGiftCertificate(modified);
    }

    @Override
    @Transactional
    public GiftCertificate create(GiftCertificate giftCertificate) throws CreateResourceException {
        try {
            if (CollectionUtils.isNotEmpty(giftCertificate.getTags())) {
                searchExistingTag(giftCertificate);
            }
            Certificate certificate = certificateRepository.save(CertificateMapper.CERTIFICATE_MAPPER.giftCertificateToCertificate(giftCertificate));
            return CertificateMapper.CERTIFICATE_MAPPER.certificateToGiftCertificate(certificate);
        } catch (EntityRetrievalException e) {
            logger.error("Failed to create certificate", e);
            throw new CreateResourceException("Failed to create certificate", e);
        }
    }

    @Override
    public GiftCertificate findById(Long id) throws ResourceNotFoundException {
        return certificateRepository.findById(id)
                .map(CertificateMapper.CERTIFICATE_MAPPER::certificateToGiftCertificate)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void delete(Long id) throws DeleteResourceException {
        certificateRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        certificateRepository.deleteById(id);
    }

    @Override
    public List<GiftCertificate> findAll(Pageable pageable) throws ResourceNotFoundException {
        return certificateRepository.findAll(pageable).get()
                .map(CertificateMapper.CERTIFICATE_MAPPER::certificateToGiftCertificate)
                .collect(Collectors.toList());
    }

    @Override
    public List<GiftCertificate> findCertificateByParams(SearchAndSortCertificateParams params, Pageable pageable) throws ResourceNotFoundException {
        try {
            List<GiftCertificate> giftCertificates;
            if (isAllParamsEmpty(params)) {
                giftCertificates = findAll(pageable);
            } else {
                giftCertificates = certificateRepository
                        .findByParams(params, pageable).stream()
                        .map(CertificateMapper.CERTIFICATE_MAPPER::certificateToGiftCertificate)
                        .collect(Collectors.toList());
            }
            return giftCertificates;
        } catch (EntityRetrievalException e) {
            logger.error("Failed to find certificate by params", e);
            throw new ResourceNotFoundException("Failed to find certificate by params", e);
        }
    }

    private void searchExistingTag(GiftCertificate giftCertificate) {
        if (CollectionUtils.isNotEmpty(giftCertificate.getTags())) {
            for (GiftTag giftTag : giftCertificate.getTags()) {
                Optional.ofNullable(giftTag.getName()).orElseThrow(CreateResourceException::new);
                Tag byName = tagRepository.findTagByName(giftTag.getName());
                if (byName != null) {
                    throw new ExistEntityException();
                }
            }
        }
    }

    private boolean isAllParamsEmpty(SearchAndSortCertificateParams params) {
        return Stream.of(params.getTags(), params.getName(), params.getDescription(),
                params.getSort())
                .allMatch(Objects::isNull);
    }
}
