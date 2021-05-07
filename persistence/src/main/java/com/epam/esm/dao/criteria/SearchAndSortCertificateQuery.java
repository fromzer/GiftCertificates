package com.epam.esm.dao.criteria;

import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.entity.Certificate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class SearchAndSortCertificateQuery implements QueryConstructor<SearchAndSortCertificateParams, Certificate> {
    private static final String MINUS = "-";
    private static final String EMPTY = "";

    @Override
    public void createQuery(SearchAndSortCertificateParams params, CriteriaBuilder cb, CriteriaQuery<Certificate> cr, Root<Certificate> root, Predicate predicate) {
        Optional.ofNullable(params.getSort())
                .ifPresentOrElse(
                        sort -> paramsSortIsNotEmpty(params, cb, cr, root, predicate),
                        () -> isEmptySortParams(cb, cr, root, predicate));
    }

    private void paramsSortIsNotEmpty(SearchAndSortCertificateParams params, CriteriaBuilder cb, CriteriaQuery<Certificate> cr, Root<Certificate> root, Predicate predicateSearchParams) {
        String sortColumn = params.getSort().replace(MINUS, EMPTY).trim();
        if (params.getSort().startsWith(MINUS)) {
            cr.select(root).where(cb.and(predicateSearchParams)).orderBy(cb.desc(root.get(sortColumn)));
        } else {
            cr.select(root).where(cb.and(predicateSearchParams)).orderBy(cb.asc(root.get(sortColumn)));
        }
    }

    private void isEmptySortParams(CriteriaBuilder cb, CriteriaQuery<Certificate> cr, Root<Certificate> root, Predicate predicateSearchParams) {
        Optional.ofNullable(predicateSearchParams)
                .ifPresentOrElse(
                        params -> cr.select(root).where(cb.and(params)),
                        () -> cr.select(root).where()
                );
    }
}
