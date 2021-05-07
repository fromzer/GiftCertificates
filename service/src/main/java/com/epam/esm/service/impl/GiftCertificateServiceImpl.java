package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ExistEntityException;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.entity.Certificate;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.DeleteResourceException;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.exception.UpdateResourceException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftTag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.utils.GiftServiceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

    private final GiftCertificateDao giftCertificateDAO;
    private final GiftTagDao tagDAO;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao giftCertificateDAO, GiftTagDao tagDAO) {
        this.giftCertificateDAO = giftCertificateDAO;
        this.tagDAO = tagDAO;
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificate giftCertificate, Long id) throws UpdateResourceException {
        searchExistingTag(giftCertificate);
        GiftCertificate existing = Optional.ofNullable(findById(id))
                .map(certificate -> {
                    Set<GiftTag> tags = certificate.getTags();
                    GiftServiceUtils.copyNonNullProperties(giftCertificate, certificate);
                    certificate.setLastUpdateDate(ZonedDateTime.now(ZoneId.of(ZONE)));
                    Optional.ofNullable(tags)
                            .map(previousTags -> certificate.getTags().addAll(previousTags));
                    return certificate;
                })
                .orElseThrow(ResourceNotFoundException::new);
        Certificate modified = giftCertificateDAO.update(modelMapper.map(existing, Certificate.class));
        return modelMapper.map(modified, GiftCertificate.class);
    }

    @Override
    @Transactional
    public Long create(GiftCertificate giftCertificate) throws CreateResourceException {
        try {
            if (CollectionUtils.isNotEmpty(giftCertificate.getTags())) {
                searchExistingTag(giftCertificate);
            }
            return giftCertificateDAO.create(modelMapper.map(giftCertificate, Certificate.class));
        } catch (EntityRetrievalException e) {
            logger.error("Failed to create certificate", e);
            throw new CreateResourceException("Failed to create certificate", e);
        }
    }

    @Override
    public GiftCertificate findById(Long id) throws ResourceNotFoundException {
        try {
            Certificate byId = Optional.ofNullable(giftCertificateDAO.findById(id))
                    .orElseThrow(ResourceNotFoundException::new);
            return modelMapper.map(byId, GiftCertificate.class);
        } catch (EntityRetrievalException e) {
            logger.error("Failed to find certificate by id", e);
            throw new ResourceNotFoundException("Failed to find certificate by id", e);
        }
    }

    @Override
    public void delete(Long id) throws DeleteResourceException {
        Certificate byId = Optional.ofNullable(giftCertificateDAO.findById(id))
                .orElseThrow(ResourceNotFoundException::new);
        giftCertificateDAO.delete(byId);
    }

    @Override
    public List<GiftCertificate> findAll(Pageable pageable) throws ResourceNotFoundException {
        List<Certificate> certificates = giftCertificateDAO.findAll(pageable);
        List<GiftCertificate> giftCertificates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(certificates)) {
            giftCertificates = certificates.stream()
                    .map(cert -> modelMapper.map(cert, GiftCertificate.class))
                    .collect(Collectors.toList());
        }
        return giftCertificates;
    }

    @Override
    public List<GiftCertificate> findCertificateByParams(SearchAndSortCertificateParams params, Pageable pageable) throws ResourceNotFoundException {
        try {
            List<GiftCertificate> giftCertificates;
            if (isAllParamsEmpty(params)) {
                giftCertificates = findAll(pageable);
            } else {
                giftCertificates = giftCertificateDAO
                        .findEntitiesByParams(params, pageable).stream()
                        .map(certificate -> modelMapper.map(certificate, GiftCertificate.class))
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
                Tag byName = tagDAO.findByName(giftTag.getName());
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
