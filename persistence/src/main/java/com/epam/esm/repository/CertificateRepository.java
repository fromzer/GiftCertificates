package com.epam.esm.repository;

import com.epam.esm.entity.Certificate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends PagingAndSortingRepository<Certificate, Long>, CertificateRepositoryCustom {
}
