package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.exception.CreateEntityException;
import com.epam.esm.exception.DeleteEntityException;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.UpdateEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
@Transactional
class CertificateDAOImplTest {
    @Autowired
    private GiftCertificateDao certificateDAO;
    @Autowired
    private GiftTagDAOImpl giftTagDAOImpl;
    private Pageable pageable;

    private Certificate certificateForSearchByParams;
    private Tag byId;
    private SearchAndSortCertificateParams searchParamsByTag;
    private SearchAndSortCertificateParams searchParamsByName;
    private SearchAndSortCertificateParams searchParamsByDescription;

    private Certificate certificate = Certificate.builder()
            .name("New certificate name")
            .description("New certificate description")
            .price(BigDecimal.valueOf(12.13))
            .duration(1)
            .createDate(ZonedDateTime.now(ZoneId.systemDefault()))
            .lastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()))
            .build();

    @BeforeEach
    public void create() {
        pageable = new Pageable(1, 10);
        byId = giftTagDAOImpl.findById(2L);
        certificateForSearchByParams = certificateDAO.findById(1L);
        searchParamsByTag = new SearchAndSortCertificateParams(byId.getName(), null, null, null);
        searchParamsByName = new SearchAndSortCertificateParams(null, certificateForSearchByParams.getName(), null, null);
        searchParamsByDescription = new SearchAndSortCertificateParams(null, null, certificateForSearchByParams.getDescription(), null);
    }

    @Test
    void shouldUpdateOnlyName() throws EntityRetrievalException, UpdateEntityException {
        Certificate expected = certificateDAO.update(Certificate.builder().id(2l).name("QWERTY").build());
        Certificate actual = certificateDAO.findById(2L);
        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateNameAndDescription() throws EntityRetrievalException, UpdateEntityException {
        Certificate expected = certificateDAO.findById(2l);
        expected.setName("New Name");
        Certificate actual = certificateDAO.findById(2L);
        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateAllFieldsAndCreateTag() throws EntityRetrievalException, UpdateEntityException {
        Certificate certificate = certificateDAO.findById(6L);
        certificate.setName("New Name");
        certificate.setDescription("New Description");
        certificate.setDuration(77);
        certificate.setTags(new LinkedHashSet<>());
        Tag tagDTO = Tag.builder()
                .name("Very")
                .build();
        certificate.getTags().add(tagDTO);
        Certificate expected = certificateDAO.update(certificate);
        Certificate actual = certificateDAO.findById(6L);
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldCreateCertificate() throws CreateEntityException, EntityRetrievalException {
        certificateDAO.create(certificate);
        SearchAndSortCertificateParams params = new SearchAndSortCertificateParams(null, certificate.getName(), null, null);
        List<Certificate> entitiesByParams = certificateDAO.findEntitiesByParams(params, pageable);
        Certificate actual = entitiesByParams.get(0);
        assertEquals(certificate.getName(), actual.getName());
        assertEquals(certificate.getDescription(), actual.getDescription());
        assertEquals(certificate.getPrice(), actual.getPrice());
        assertEquals(certificate.getDuration(), actual.getDuration());
    }

    @Test
    void shouldFindCertificateById() throws EntityRetrievalException {
        Certificate excepted = certificateDAO.findById(1L);
        assertNotNull(excepted);
        assertEquals(excepted.getName(), "TestCertificate");
    }

    @Test
    void shouldNotFindReturnNull() throws EntityRetrievalException {
        assertEquals(certificateDAO.findById(77L), null);
    }

    @Test
    void shouldDeleteCertificate() throws DeleteEntityException, EntityRetrievalException {
        Certificate cert = Certificate.builder()
                .id(6L)
                .build();
        certificateDAO.delete(cert);
        assertEquals(certificateDAO.findById(cert.getId()), null);
    }

    @Test
    void shouldFindAllCertificates() throws EntityRetrievalException {
        Certificate actual = certificateDAO.findById(1L);
        List<Certificate> expected = certificateDAO.findAll(pageable);
        assertEquals(expected.get(0), actual);
    }

    @Test
    void shouldFindCertificateByParams() throws EntityRetrievalException {
        List<Certificate> certificateByDescription = certificateDAO.findEntitiesByParams(searchParamsByDescription, pageable);
        assertEquals(certificateByDescription.get(0).getDuration(), certificateForSearchByParams.getDuration());
    }

    @Test
    void shouldFindCertificateByName() throws EntityRetrievalException {
        List<Certificate> certificateByName = certificateDAO.findEntitiesByParams(searchParamsByName, pageable);
        assertEquals(certificateByName.get(0).getName(), certificateForSearchByParams.getName());
    }

    @Test
    void shouldFindCertificateByTag() throws EntityRetrievalException {
        List<Certificate> certificateByTagName = certificateDAO.findEntitiesByParams(searchParamsByTag, pageable);
        assertEquals(certificateByTagName.get(0).getId(), certificateForSearchByParams.getId());
    }
}