package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.exception.ExistEntityException;
import com.epam.esm.model.Pageable;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.CreateEntityException;
import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftTag;
import com.epam.esm.service.GiftTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class GiftTagServiceImpl implements GiftTagService {
    private final GiftTagDao tagDAO;
    private ModelMapper modelMapper = new ModelMapper();

    public GiftTagServiceImpl(GiftTagDao tagDAO) {
        this.tagDAO = tagDAO;
    }

    @Override
    @Transactional
    public Long create(GiftTag entity) throws CreateResourceException {
        try {
            isTagExist(entity);
            Tag map = modelMapper.map(entity, Tag.class);
            return tagDAO.create(map);
        } catch (CreateEntityException e) {
            log.error("Failed to create tag", e);
            throw new CreateResourceException("Failed to create tag", e);
        }
    }

    @Override
    public GiftTag findById(Long id) throws ResourceNotFoundException {
        Tag byId = Optional.ofNullable(tagDAO.findById(id))
                .orElseThrow(ResourceNotFoundException::new);
        return modelMapper.map(byId, GiftTag.class);
    }

    @Override
    public GiftTag findByName(String name) throws ResourceNotFoundException {
        try {
            return modelMapper.map(tagDAO.findByName(name), GiftTag.class);
        } catch (EntityRetrievalException e) {
            log.error("Failed to find tag", e);
            throw new ResourceNotFoundException("Failed to find tag", e);
        }
    }

    @Override
    public void delete(Long id) {
        Tag byId = Optional.ofNullable(tagDAO.findById(id))
                .orElseThrow(ResourceNotFoundException::new);
        tagDAO.delete(byId);
    }

    @Override
    public List<GiftTag> findAll(Pageable pageable) throws ResourceNotFoundException {
        List<Tag> tags = tagDAO.findAll(pageable);
        List<GiftTag> convertedAllTags = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tags)) {
            convertedAllTags = tags.stream()
                    .map(tag -> modelMapper.map(tag, GiftTag.class))
                    .collect(Collectors.toList());
        }
        return convertedAllTags;
    }

    private void isTagExist(GiftTag entity) {
        Tag byName = tagDAO.findByName(entity.getName());
        if (byName != null) {
            throw new ExistEntityException();
        }
    }
}
