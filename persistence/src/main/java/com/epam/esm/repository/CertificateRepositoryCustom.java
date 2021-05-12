package com.epam.esm.repository;

import com.epam.esm.entity.Certificate;
import com.epam.esm.model.SearchAndSortCertificateParams;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CertificateRepositoryCustom {
    List<Certificate> findByParams(SearchAndSortCertificateParams params, Pageable pageable);
}
