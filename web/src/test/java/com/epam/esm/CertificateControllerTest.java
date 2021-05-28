package com.epam.esm;

import com.epam.esm.controller.CertificateController;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftTag;
import com.epam.esm.model.ModifiedGiftCertificate;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.service.GiftCertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CertificateControllerTest {
    @Autowired
    WebApplicationContext applicationContext;

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CertificateController controller;
    @Mock
    private Pageable pageable;
    @Mock
    private SearchAndSortCertificateParams params;
    @MockBean
    private GiftCertificateService certificateService;
    private GiftCertificate certificate;
    private ModifiedGiftCertificate modifiedGiftCertificate;

    @BeforeEach
    public void create() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        GiftTag tag = GiftTag.builder()
                .id(1L)
                .name("moto")
                .build();
        Set<GiftTag> giftTags = new LinkedHashSet<>();
        giftTags.add(tag);
        certificate = GiftCertificate.builder()
                .id(1L)
                .name("new NAME")
                .description("car")
                .price(BigDecimal.valueOf(11.00))
                .duration(7)
                .tags(giftTags)
                .build();
        modifiedGiftCertificate = ModifiedGiftCertificate.builder()
                .id(1L)
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

    @WithMockUser(roles = "ADMIN")
    @Test
    public void shouldCreateCertificate() throws Exception {
        when(certificateService.create(any())).thenReturn(any());
        mockMvc.perform(
                post("/api/v1/certificates")
                        .content(objectMapper.writeValueAsString(certificate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void shouldUpdateCertificate() throws Exception {
        when(certificateService.update(any(), anyLong())).thenReturn(certificate);
        mockMvc.perform(
                patch("/api/v1/certificates/{id}", modifiedGiftCertificate.getId())
                        .content(objectMapper.writeValueAsString(modifiedGiftCertificate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void shouldDeleteCertificate() throws Exception {
        when(certificateService.findById(any())).thenReturn(certificate);
        mockMvc.perform(
                delete("/api/v1/certificates/{id}", certificate.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldGetCertificates() throws Exception {
        Page<GiftCertificate> certificatePage = new PageImpl<>(Stream.of(certificate).collect(Collectors.toList()));
        when(certificateService.findCertificateByParams(any(), any())).thenReturn(certificatePage);
        mockMvc.perform(get("/api/v1/certificates")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void notFoundCertificateById() throws Exception {
        when(certificateService.findById(any())).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/certificates/{id}", 1000L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
