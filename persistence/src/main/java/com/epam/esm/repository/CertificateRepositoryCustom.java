package com.epam.esm.repository;

import com.epam.esm.entity.Certificate;
import com.epam.esm.model.SearchAndSortCertificateParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CertificateRepositoryCustom {

    Page<Certificate> findByParams(SearchAndSortCertificateParams params, Pageable pageable);
}
