package com.epam.esm;

import com.epam.esm.controller.TagController;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftTag;
import com.epam.esm.service.GiftTagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TagControllerTest {
    @Autowired
    WebApplicationContext applicationContext;

    private MockMvc mockMvc;
    @Autowired
    private TagController tagController;
    @MockBean
    private GiftTagService tagService;
    @Autowired
    private ObjectMapper objectMapper;
    private GiftTag tag;

    @BeforeEach
    public void create() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        tag = GiftTag.builder()
                .id(1L)
                .name("name")
                .build();
    }

    @Test
    public void contextLoads() {
        assertThat(tagController).isNotNull();
    }

    @Test
    public void shouldGetTagById() throws Exception {
        when(tagService.findById(any())).thenReturn(tag);
        mockMvc.perform(get("/api/v1/tags/{id}", tag.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tag.getId()))
                .andExpect(jsonPath("$.name").value(tag.getName()));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void shouldCreateTag() throws Exception {
        when(tagService.create(any())).thenReturn(tag);
        mockMvc.perform(
                post("/api/v1/tags")
                        .content(objectMapper.writeValueAsString(tag))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void shouldDeleteTag() throws Exception {
        when(tagService.findById(any())).thenReturn(tag);
        mockMvc.perform(
                delete("/api/v1/tags/{id}", tag.getId()))
                .andExpect(status().isNoContent());
    }


    @Test
    public void notFoundTagById() throws Exception {
        when(tagService.findById(any())).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/tags/{id}", 1000L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetTags() throws Exception {
        Page<GiftTag> tagPage = new PageImpl<>(Stream.of(tag).collect(Collectors.toList()));
        when(tagService.findAll(any())).thenReturn(tagPage);
        mockMvc.perform(get("/api/v1/tags")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
