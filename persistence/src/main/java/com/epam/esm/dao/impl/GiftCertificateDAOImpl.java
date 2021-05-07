package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.criteria.SearchAndSortCertificatePredicate;
import com.epam.esm.dao.criteria.SearchAndSortCertificateQuery;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.entity.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class GiftCertificateDAOImpl implements GiftCertificateDao {

    private final GiftDaoBean<Certificate> giftDao;

    @Autowired
    public GiftCertificateDAOImpl(GiftDaoBean giftDao) {
        this.giftDao = giftDao;
        giftDao.setClazz(Certificate.class);
    }

    @Override
    @Transactional
    public Long create(Certificate certificate) {
        return giftDao.create(certificate);
    }

    @Override
    public void delete(Certificate certificate) {
        giftDao.delete(certificate);
    }

    @Override
    public List<Certificate> findAll(Pageable pageable) {
        return giftDao.findAll(pageable);
    }

    @Override
    public List<Certificate> findEntitiesByParams(SearchAndSortCertificateParams params, Pageable pageable) {
        return giftDao.findEntitiesByParams(params, pageable, new SearchAndSortCertificatePredicate(), new SearchAndSortCertificateQuery());
    }

    @Override
    public Certificate findById(Long id) {
        return giftDao.findById(id);
    }

    @Override
    @Transactional
    public Certificate update(Certificate certificate) {
        return giftDao.update(certificate);
    }
}
