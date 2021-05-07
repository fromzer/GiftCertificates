package com.epam.esm;

import com.epam.esm.controller.TagController;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.model.GiftTag;
import com.epam.esm.service.GiftTagService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TagController tagController;
    @MockBean
    private GiftTagService tagService;
    @Autowired
    private ObjectMapper objectMapper;
    private GiftTag tag = GiftTag.builder()
            .id(1l)
            .name("name")
            .build();

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

    @Test
    public void shouldCreateTag() throws Exception {
        when(tagService.create(any())).thenReturn(1l);
        mockMvc.perform(
                post("/api/v1/tags")
                        .content(objectMapper.writeValueAsString(tag))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteTag() throws Exception {
        when(tagService.findById(any())).thenReturn(tag);
        mockMvc.perform(
                delete("/api/v1/tags/{id}", tag.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void notFoundTagById() throws Exception {
        mockMvc.perform(get("/api/v1/tags/{id}", 1000l)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetTags() throws Exception {
        mockMvc.perform(get("/api/v1/tags")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
