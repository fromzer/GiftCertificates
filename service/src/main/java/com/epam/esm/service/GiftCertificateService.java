package com.epam.esm.service;

import com.epam.esm.exception.CreateResourceException;
import com.epam.esm.exception.DeleteResourceException;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.exception.UpdateResourceException;
import com.epam.esm.model.GiftCertificate;

import java.util.List;

/**
 * Base interface for Certificate Service
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface GiftCertificateService {
    /**
     * Create entity
     *
     * @param giftCertificate an entity of business model
     * @return entity id
     * @throws CreateResourceException if error is occurred during SQL command execution
     */
    Long create(GiftCertificate giftCertificate) throws CreateResourceException;

    /**
     * Find entity
     *
     * @return entity
     * @throws ResourceNotFoundException if fail to retrieve data from DB
     */
    GiftCertificate findById(Long id) throws ResourceNotFoundException;

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
    List<GiftCertificate> findAll(Pageable pageable) throws ResourceNotFoundException;

    /**
     * Update entity
     *
     * @param giftCertificate an DTO of business model
     * @return updated GiftCertificate
     * @throws UpdateResourceException if fail to update data
     */
    GiftCertificate update(GiftCertificate giftCertificate, Long id) throws UpdateResourceException;

    /**
     * Find entity
     *
     * @param params   the search options
     * @param pageable pagination
     * @return list of GiftCertificates
     * @throws ResourceNotFoundException if fail to retrieve data
     */
    List<GiftCertificate> findCertificateByParams(SearchAndSortCertificateParams params, Pageable pageable) throws ResourceNotFoundException;
}
