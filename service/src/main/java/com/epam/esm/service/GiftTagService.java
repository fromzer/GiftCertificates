package com.epam.esm.service;


import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.DeleteResourceException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.model.GiftTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Base interface for Tag Service
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface GiftTagService {

    /**
     * Find entity by name
     *
     * @param name the tag name
     * @return entity
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    GiftTag findByName(String name) throws ResourceNotFoundException;

    /**
     * Create entity
     *
     * @param giftTag an entity of business model
     * @return entity id
     * @throws CreateResourceException if error is occurred during SQL command execution
     */
    GiftTag create(GiftTag giftTag) throws CreateResourceException;

    /**
     * Find entity
     *
     * @return entity
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    GiftTag findById(Long id) throws ResourceNotFoundException;

    /**
     * Delete entity
     *
     * @throws DeleteResourceException if error is occurred during SQL command execution
     */
    void delete(Long id) throws DeleteResourceException, ResourceNotFoundException;

    /**
     * Find all entities
     *
     * @param pageable pagination
     * @return List of entities
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    Page<GiftTag> findAll(Pageable pageable) throws ResourceNotFoundException;
}
