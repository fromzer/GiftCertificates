package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.impl.GiftCertificateDAOImpl;
import com.epam.esm.dao.impl.GiftTagDAOImpl;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ExistEntityException;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.exception.CreateEntityException;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.DeleteEntityException;
import com.epam.esm.exception.DeleteResourceException;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.exception.UpdateEntityException;
import com.epam.esm.exception.UpdateResourceException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {
    @Mock
    private GiftCertificateDAOImpl certificateDAO;

    @Mock
    private GiftTagDAOImpl tagDAO;

    private ModelMapper modelMapper;
    private GiftCertificateServiceImpl giftCertificateService;
    private Pageable pageable;

    private Certificate correctCertificate;
    private GiftCertificate correctGiftCertificate;

    @BeforeEach
    void createCertificate() {
        modelMapper = new ModelMapper();
        giftCertificateService = new GiftCertificateServiceImpl(certificateDAO, tagDAO);
        pageable = new Pageable(1, 20);
        correctCertificate = Certificate.builder()
                .id(1l)
                .name("New certificate name")
                .description("New certificate description")
                .price(BigDecimal.valueOf(12.13))
                .duration(1)
                .build();
        correctGiftCertificate = GiftCertificate.builder()
                .id(1l)
                .name("New certificate name")
                .description("New certificate description")
                .price(BigDecimal.valueOf(12.13))
                .duration(1)
                .build();
        pageable = new Pageable(1, 10);
    }

    @Test
    void shouldUpdateOnlyName() throws UpdateEntityException, UpdateResourceException, EntityRetrievalException {
        GiftCertificate giftCertificate = GiftCertificate.builder()
                .id(1l)
                .name("new NAME")
                .build();
        Certificate certificate = Certificate.builder()
                .id(1l)
                .name("new NAME")
                .description("New certificate description")
                .price(BigDecimal.valueOf(12.13))
                .duration(1)
                .build();
        when(certificateDAO.findById(1L)).thenReturn(certificate);
        when(certificateDAO.update(any())).thenReturn(certificate);
        GiftCertificate actual = giftCertificateService.update(giftCertificate, giftCertificate.getId());
        assertEquals(certificate.getName(), actual.getName());
    }

    @Test
    void shouldNotUpdateCertificate() throws EntityRetrievalException, UpdateEntityException, UpdateResourceException {
        GiftCertificate actual = GiftCertificate.builder()
                .name("new NAME")
                .description("car")
                .build();
        assertThrows(ResourceNotFoundException.class, () -> giftCertificateService.update(actual, 77l));
    }

    @Test
    void shouldUpdateAllFieldsAndCreateTag() throws EntityRetrievalException, UpdateEntityException, UpdateResourceException {
        Tag tag = Tag.builder()
                .id(1l)
                .name("moto")
                .build();
        GiftTag giftTag = GiftTag.builder()
                .id(1l)
                .name("moto")
                .build();
        Set<Tag> tagSet = new LinkedHashSet<>();
        tagSet.add(tag);
        Set<GiftTag> giftTags = new LinkedHashSet<>();
        giftTags.add(giftTag);
        Certificate certificate = Certificate.builder()
                .id(1l)
                .name("new NAME")
                .description("car")
                .price(BigDecimal.valueOf(11.00))
                .duration(7)
                .createDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .lastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .tags(tagSet)
                .build();
        GiftCertificate giftCertificate = GiftCertificate.builder()
                .id(1l)
                .name("new NAME")
                .description("car")
                .price(BigDecimal.valueOf(11.00))
                .duration(7)
                .tags(giftTags)
                .build();
        Certificate certificateDTO = Certificate.builder()
                .id(1l)
                .name("new NAME")
                .description("car")
                .price(BigDecimal.valueOf(11.00))
                .duration(7)
                .tags(tagSet)
                .build();
        when(certificateDAO.findById(1L)).thenReturn(certificateDTO);
        when(certificateDAO.update(any())).thenReturn(certificateDTO);
        GiftCertificate update = giftCertificateService.update(giftCertificate, giftCertificate.getId());
        boolean contains = update.getTags().contains(giftTag);
        assertEquals(certificateDTO.getDescription(), update.getDescription());
        assertEquals(certificateDTO.getName(), update.getName());
        assertEquals(certificateDTO.getPrice(), update.getPrice());
        assertEquals(certificateDTO.getDuration(), update.getDuration());
        assertEquals(contains, true);
    }

    @Test
    void shouldCreateCertificate() throws CreateEntityException, CreateResourceException {
        when(certificateDAO.create(correctCertificate)).thenReturn(1l);
        assertEquals(correctCertificate.getId(), giftCertificateService.create(correctGiftCertificate));
    }

    @Test
    void shouldNotCreateCertificateDuplicateTag() throws CreateEntityException, CreateResourceException {
        GiftTag giftTag = GiftTag.builder()
                .id(1l)
                .name("A1")
                .build();
        Set<GiftTag> giftTags = new LinkedHashSet<>();
        giftTags.add(giftTag);
        GiftCertificate giftCertificate = GiftCertificate.builder()
                .id(1l)
                .name("new NAME")
                .description("car")
                .price(BigDecimal.valueOf(11.00))
                .duration(7)
                .tags(giftTags)
                .build();
        when(certificateDAO.create(any())).thenThrow(ExistEntityException.class);
        assertThrows(ExistEntityException.class, () -> giftCertificateService.create(giftCertificate));
    }

    @Test
    void shouldNotCreateCertificate() throws CreateEntityException, CreateResourceException {
        when(certificateDAO.create(any())).thenThrow(CreateResourceException.class);
        assertThrows(CreateResourceException.class, () -> giftCertificateService.create(correctGiftCertificate));
    }

    @Test
    void ShouldFindCertificateById() throws ResourceNotFoundException, EntityRetrievalException {
        when(certificateDAO.findById(anyLong())).thenReturn(correctCertificate);
        GiftCertificate actual = giftCertificateService.findById(1L);
        assertEquals(correctGiftCertificate.getId(), actual.getId());
    }

    @Test
    void ShouldNotFindCertificateById() throws ResourceNotFoundException, EntityRetrievalException {
        when(certificateDAO.findById(anyLong())).thenReturn(correctCertificate);
        GiftCertificate actual = giftCertificateService.findById(1L);
        GiftCertificate ex = GiftCertificate.builder()
                .id(1l)
                .name("New name")
                .description("New certificate description")
                .price(BigDecimal.valueOf(12.00))
                .duration(1)
                .build();
        assertNotEquals(ex, actual);
    }

    @Test
    void shouldDeleteCertificateConvertResourceException() throws DeleteEntityException {
        assertThrows(ResourceNotFoundException.class, () -> giftCertificateService.delete(correctGiftCertificate.getId()));
    }

    @Test
    void shouldDeleteCertificate() throws DeleteEntityException {
        when(certificateDAO.findById(anyLong())).thenReturn(correctCertificate);
        giftCertificateService.delete(correctGiftCertificate.getId());
        verify(certificateDAO, times(1)).delete(modelMapper.map(correctGiftCertificate, Certificate.class));
    }

    @Test
    void shouldFindAllCertificates() throws EntityRetrievalException, ResourceNotFoundException {
        List<Certificate> certificateDTOList = new ArrayList<>();
        certificateDTOList.add(correctCertificate);
        when(certificateDAO.findAll(any())).thenReturn(certificateDTOList);
        assertEquals(1, giftCertificateService.findAll(pageable).size());
    }

    @Test
    void shouldNotFindAllCertificates() throws EntityRetrievalException, ResourceNotFoundException {
        when(certificateDAO.findAll(any())).thenReturn(any());
        ArrayList<GiftCertificate> excepted = new ArrayList<>();
        List<GiftCertificate> actual = giftCertificateService.findAll(pageable);
        assertEquals(excepted, actual);
    }

    @Test
    void shouldFindCertificateByTag() throws EntityRetrievalException, ResourceNotFoundException {
        Tag tagDTO = Tag.builder()
                .id(1l)
                .name("moto")
                .build();
        GiftTag giftTag = GiftTag.builder()
                .id(1l)
                .name("moto")
                .build();
        Set<Tag> tagDTOSet = new LinkedHashSet<>();
        tagDTOSet.add(tagDTO);
        Certificate certificateDTO = Certificate.builder()
                .id(2l)
                .name("Hello")
                .description("World")
                .price(BigDecimal.valueOf(12.00))
                .duration(1)
                .tags(tagDTOSet)
                .build();
        List<Certificate> certificateDTOList = new ArrayList<>();
        certificateDTOList.add(certificateDTO);
        when(certificateDAO.findEntitiesByParams(any(SearchAndSortCertificateParams.class), any())).thenReturn(certificateDTOList);
        SearchAndSortCertificateParams options = new SearchAndSortCertificateParams("moto", null, null, null);
        GiftCertificate giftCertificateFindByTag = giftCertificateService.findCertificateByParams(options, pageable).get(0);
        boolean containTag = giftCertificateFindByTag.getTags().contains(giftTag);
        assertEquals(containTag, true);
    }

    @Test
    void shouldFindCertificateByName() throws EntityRetrievalException, ResourceNotFoundException {
        Tag tagDTO = Tag.builder()
                .id(1l)
                .name("moto")
                .build();
        Set<Tag> tagDTOSet = new LinkedHashSet<>();
        tagDTOSet.add(tagDTO);
        Certificate certificateDTO = Certificate.builder()
                .id(2l)
                .name("Hello")
                .description("World")
                .price(BigDecimal.valueOf(12.00))
                .duration(1)
                .tags(tagDTOSet)
                .build();
        List<Certificate> certificateDTOList = new ArrayList<>();
        certificateDTOList.add(certificateDTO);
        when(certificateDAO.findEntitiesByParams(any(SearchAndSortCertificateParams.class), any())).thenReturn(certificateDTOList);
        SearchAndSortCertificateParams options = new SearchAndSortCertificateParams(null, "Hello", null, null);
        GiftCertificate giftCertificateFindByName = giftCertificateService.findCertificateByParams(options, pageable).get(0);
        boolean findByName = giftCertificateFindByName.getName().equals("Hello");
        assertEquals(findByName, true);
    }

    @Test
    void shouldFindCertificateByDescription() throws EntityRetrievalException, ResourceNotFoundException {
        Tag tagDTO = Tag.builder()
                .id(1l)
                .name("moto")
                .build();
        Set<Tag> tagDTOSet = new LinkedHashSet<>();
        tagDTOSet.add(tagDTO);
        Certificate certificateDTO = Certificate.builder()
                .id(2l)
                .name("Hello")
                .description("World")
                .price(BigDecimal.valueOf(12.00))
                .duration(1)
                .tags(tagDTOSet)
                .build();
        List<Certificate> certificateDTOList = new ArrayList<>();
        certificateDTOList.add(certificateDTO);
        when(certificateDAO.findEntitiesByParams(any(SearchAndSortCertificateParams.class), any())).thenReturn(certificateDTOList);
        SearchAndSortCertificateParams options = new SearchAndSortCertificateParams(null, null, "World", null);
        GiftCertificate giftCertificateFindByName = giftCertificateService.findCertificateByParams(options, pageable).get(0);
        boolean findByDescription = giftCertificateFindByName.getDescription().equals("World");
        assertEquals(findByDescription, true);
    }
}