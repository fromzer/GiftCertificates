package com.epam.esm.service.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.CreateEntityException;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.DeleteEntityException;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftTag;
import com.epam.esm.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftTagServiceImplTest {
    @Mock
    TagRepository tagRepository;

    @Mock
    private Pageable pageable;
    GiftTagServiceImpl giftTagService;
    Tag correctTag;
    GiftTag correctGiftTag;

    @BeforeEach
    void createTag() {
        giftTagService = new GiftTagServiceImpl(tagRepository);
        correctTag = new Tag(1L, "name");
        correctGiftTag = new GiftTag(1L, "name");
    }

    @Test
    void shouldCreateTag() throws CreateEntityException, CreateResourceException {
        when(tagRepository.save(correctTag)).thenReturn(correctTag);
        assertEquals(correctTag.getName(), giftTagService.create(correctGiftTag).getName());
    }

    @Test
    void shouldNotCreateTag() throws CreateEntityException, CreateResourceException {
        GiftTag actual = new GiftTag();
        when(tagRepository.save(any())).thenThrow(CreateEntityException.class);
        assertThrows(CreateResourceException.class, () -> giftTagService.create(actual));
    }

    @Test
    void shouldFindTagById() throws EntityRetrievalException, ResourceNotFoundException {
        when(tagRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(correctTag));
        GiftTag actual = giftTagService.findById(1L);
        assertEquals(correctGiftTag, actual);
    }

    @Test
    void shouldNotFindTagById() throws EntityRetrievalException, ResourceNotFoundException {
        when(tagRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(correctTag));
        GiftTag actual = giftTagService.findById(1L);
        GiftTag ex = new GiftTag(1L, "exception");
        assertNotEquals(ex, actual);
    }

    @Test
    void shouldFindTagByName() throws EntityRetrievalException, ResourceNotFoundException {
        when(tagRepository.findTagByName(anyString())).thenReturn(correctTag);
        GiftTag actual = giftTagService.findByName("name");
        assertEquals(correctGiftTag, actual);
    }

    @Test
    void shouldNotFindTagByName() throws EntityRetrievalException, ResourceNotFoundException {
        when(tagRepository.findTagByName(anyString())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> giftTagService.findByName("name"));
    }

    @Test
    void shouldDeleteTagReturnException() throws DeleteEntityException {
        when(tagRepository.findById(correctGiftTag.getId())).thenThrow(EntityRetrievalException.class);
        assertThrows(EntityRetrievalException.class, () -> giftTagService.delete(correctGiftTag.getId()));
    }

    @Test
    void shouldFindAllTags() throws EntityRetrievalException, ResourceNotFoundException {
        List<Tag> tagDTOList = new ArrayList<>();
        tagDTOList.add(correctTag);
        Page<Tag> tagPage = new PageImpl<>(tagDTOList);
        when(tagRepository.findAll((Pageable) any())).thenReturn(tagPage);
        assertEquals(1, giftTagService.findAll(pageable).size());
    }
}