package com.epam.esm.dao;

import com.epam.esm.entity.Certificate;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortCertificateParams;

import java.util.List;

/**
 * Base interface class for GiftCertificateDAO
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface GiftCertificateDao {
    /**
     * Create certificate in DB
     *
     * @param certificate a certificate of business model
     * @return entry id
     */
    Long create(Certificate certificate);

    /**
     * Update certificate in DB
     *
     * @param certificate a certificate of business model
     * @return updated certificateDTO
     */
    Certificate update(Certificate certificate);

    /**
     * Find certificate in DB
     *
     * @param id certificate id
     * @return certificate
     */
    Certificate findById(Long id);

    /**
     * Find all certificates in DB
     *
     * @param pageable pagination
     * @return List of certificates
     */
    List<Certificate> findAll(Pageable pageable);

    /**
     * Find entry in table
     *
     * @param params   the search and sort options
     * @param pageable pagination
     * @return list of Certificate
     */
    List<Certificate> findEntitiesByParams(SearchAndSortCertificateParams params, Pageable pageable);

    /**
     * Delete certificate in DB
     *
     * @param certificate the certificate
     */
    void delete(Certificate certificate);
}
