package com.epam.esm;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.service.GiftCertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CertificateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CertificateController controller;
    @MockBean
    private GiftCertificateService certificateService;
    private GiftTag tag;
    private GiftCertificate certificate;

    @BeforeEach
    public void create() {
        tag = GiftTag.builder()
                .id(1l)
                .name("moto")
                .build();
        Set<GiftTag> giftTags = new LinkedHashSet<>();
        giftTags.add(tag);
        certificate = GiftCertificate.builder()
                .id(1l)
                .name("new NAME")
                .description("car")
                .price(BigDecimal.valueOf(11.00))
                .duration(7)
                .tags(giftTags)
                .build();
    }

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void shouldGetCertificateById() throws Exception {
        when(certificateService.findById(any())).thenReturn(certificate);
        mockMvc.perform(get("/api/v1/certificates/{id}", certificate.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(certificate.getId()))
                .andExpect(jsonPath("$.name").value(certificate.getName()))
                .andExpect(jsonPath("$.description").value(certificate.getDescription()))
                .andExpect(jsonPath("$.duration").value(certificate.getDuration()));
    }

    @Test
    public void shouldCreateCertificate() throws Exception {
        when(certificateService.create(any())).thenReturn(1l);
        mockMvc.perform(
                post("/api/v1/certificates")
                        .content(objectMapper.writeValueAsString(certificate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateCertificate() throws Exception {
        when(certificateService.update(any(), anyLong())).thenReturn(certificate);
        mockMvc.perform(
                patch("/api/v1/certificates/{id}", certificate.getId())
                        .content(objectMapper.writeValueAsString(certificate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteCertificate() throws Exception {
        when(certificateService.findById(any())).thenReturn(certificate);
        mockMvc.perform(
                delete("/api/v1/certificates/{id}", certificate.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldGetAllCertificates() {
        SearchAndSortCertificateParams params = new SearchAndSortCertificateParams(null, null, null, null);
        GiftCertificate giftCertificateFirst = certificate;
        GiftCertificate giftCertificateSecond = GiftCertificate.builder()
                .id(1l)
                .name("new NAME")
                .description("car")
                .price(BigDecimal.valueOf(11.00))
                .duration(7)
                .build();
        List<GiftCertificate> giftCertificates = new ArrayList<>();
        giftCertificates.add(giftCertificateFirst);
        giftCertificates.add(giftCertificateSecond);
        Pageable pageable = new Pageable();
        when(certificateService.findCertificateByParams(params, pageable)).thenReturn(giftCertificates);
        ResponseEntity<CollectionModel<EntityModel<GiftCertificate>>> certificates = controller.getCertificatesWithParameters(params, new Pageable());
        List<GiftCertificate> collect = certificates.getBody().getContent().stream()
                .map(model -> model.getContent())
                .collect(Collectors.toList());
        assertThat(collect.size()).isEqualTo(giftCertificates.size());
        assertThat(collect.get(1).getName()).isEqualTo(giftCertificates.get(1).getName());
        assertThat(collect.get(0).getName()).isEqualTo(giftCertificates.get(0).getName());
    }

    @Test
    public void shouldGetCertificates() throws Exception {
        mockMvc.perform(get("/api/v1/certificates")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void notFoundCertificateById() throws Exception {
        mockMvc.perform(get("/api/v1/certificates/{id}", 1000l)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
