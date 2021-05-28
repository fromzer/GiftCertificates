package com.epam.esm.repository;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DeleteEntityException;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.UpdateEntityException;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.repository.impl.CertificateRepositoryCustomImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

//@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql({"classpath:create_tables.sql"})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class CertificateRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CertificateRepositoryCustomImpl certificateRepositoryCustom;
    @Mock
    private Pageable pageable;

    private Certificate certificateForSearchByParams;
    private Tag byId;
    private SearchAndSortCertificateParams searchParamsByTag;
    private SearchAndSortCertificateParams searchParamsByName;
    private SearchAndSortCertificateParams searchParamsByDescription;

    private Tag tag = Tag.builder().name("TestTag").build();
    private Certificate certificate = Certificate.builder()
            .name("New certificate name")
            .description("New certificate description")
            .price(BigDecimal.valueOf(12.13))
            .duration(1)
            .createDate(ZonedDateTime.now(ZoneId.systemDefault()))
            .lastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()))
            .tags(Stream.of(tag).collect(Collectors.toSet()))
            .build();


    @BeforeEach
    public void create() {
        entityManager.getEntityManager().persist(certificate);
        entityManager.flush();
        searchParamsByTag = new SearchAndSortCertificateParams(tag.getName(), null, null, null);
        searchParamsByName = new SearchAndSortCertificateParams(null, certificate.getName(), null, null);
        searchParamsByDescription = new SearchAndSortCertificateParams(null, null, certificate.getDescription(), null);
    }

    @Test
    void shouldUpdateOnlyName() throws EntityRetrievalException, UpdateEntityException {
        Certificate expected = certificateRepository.save(Certificate.builder().id(1l).name("QWERTY").build());
        Certificate actual = certificateRepository.findById(1L).get();
        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateNameAndDescription() throws EntityRetrievalException, UpdateEntityException {
        Certificate expected = certificateRepository.findById(1L).get();
        expected.setName("New Name");
        Certificate actual = certificateRepository.findById(1L).get();
        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateAllFieldsAndCreateTag() throws EntityRetrievalException, UpdateEntityException {
        Certificate certificate = certificateRepository.findById(1L).get();
        certificate.setName("New Name");
        certificate.setDescription("New Description");
        certificate.setDuration(77);
        certificate.setTags(new LinkedHashSet<>());
        Tag tagDTO = Tag.builder()
                .name("Very")
                .build();
        certificate.getTags().add(tagDTO);
        Certificate expected = certificateRepository.save(certificate);
        Certificate actual = certificateRepository.findById(1L).get();
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldFindCertificateById() throws EntityRetrievalException {
        Certificate excepted = certificateRepository.findById(1L).get();
        assertNotNull(excepted);
        assertEquals(excepted.getName(), "New certificate name");
    }

    @Test
    void shouldNotFindReturnNull() throws EntityRetrievalException {
        assertEquals(certificateRepository.findById(77L), Optional.empty());
    }

    @Test
    void shouldDeleteCertificate() throws DeleteEntityException, EntityRetrievalException {
        Certificate cert = Certificate.builder()
                .id(1L)
                .build();
        certificateRepository.delete(cert);
        assertEquals(certificateRepository.findById(cert.getId()), Optional.empty());
    }

    @Test
    void shouldFindAllCertificates() throws EntityRetrievalException {
        Certificate actual = certificateRepository.findById(1L).get();
        Page<Certificate> all = certificateRepository.findAll(pageable);
        List<Certificate> expected = all.getContent();
        assertEquals(expected.get(0), actual);
    }
}