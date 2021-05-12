package com.epam.esm.service.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.CreateEntityException;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.ExistEntityException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.mapper.TagMapper;
import com.epam.esm.model.GiftTag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.GiftTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class GiftTagServiceImpl implements GiftTagService {
    private final TagRepository tagRepository;

    public GiftTagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public GiftTag create(GiftTag entity) throws CreateResourceException {
        try {
            isTagExist(entity);
            Tag map = TagMapper.TAG_MAPPER.giftTagToTag(entity);
            return TagMapper.TAG_MAPPER.tagToGiftTag(tagRepository.save(map));
        } catch (CreateEntityException e) {
            log.error("Failed to create tag", e);
            throw new CreateResourceException("Failed to create tag", e);
        }
    }

    @Override
    public GiftTag findById(Long id) throws ResourceNotFoundException {
        Tag byId = tagRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return TagMapper.TAG_MAPPER.tagToGiftTag(byId);
    }

    @Override
    public GiftTag findByName(String name) throws ResourceNotFoundException {
        return TagMapper.TAG_MAPPER.tagToGiftTag(tagRepository.findTagByName(name));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        tagRepository.delete(tag);
    }

    @Override
    public List<GiftTag> findAll(Pageable pageable) throws ResourceNotFoundException {
        return tagRepository.findAll(pageable).get()
                .map(TagMapper.TAG_MAPPER::tagToGiftTag)
                .collect(Collectors.toList());
    }

    private void isTagExist(GiftTag entity) {
        Tag byName = tagRepository.findTagByName(entity.getName());
        if (byName != null) {
            throw new ExistEntityException();
        }
    }
}
