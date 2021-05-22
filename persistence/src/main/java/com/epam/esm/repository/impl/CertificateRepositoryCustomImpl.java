package com.epam.esm.repository.impl;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.model.SearchAndSortCertificateParams;
import com.epam.esm.repository.CertificateRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CertificateRepositoryCustomImpl implements CertificateRepositoryCustom {
    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String DESCRIPTION = "description";
    private static final String PERCENT = "%";
    private static final String MINUS = "-";
    private static final String EMPTY = "";

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @SuppressWarnings({"unchecked"})
    public Page<Certificate> findByParams(SearchAndSortCertificateParams params, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Certificate> cr = cb.createQuery(Certificate.class);
        Root<Certificate> root = cr.from(Certificate.class);
        Predicate predicateSearchParams = createPredicate(params, cb, root);
        createQuery(params, cb, cr, root, predicateSearchParams);
        Query query = entityManager.createQuery(cr);
        int totalRows = query.getResultList().size();
        query.setFirstResult(pageable.getPageNumber());
        query.setMaxResults(pageable.getPageSize());
        List<Certificate> resultList = query.getResultList();
        List<Certificate> distinctResult = resultList.stream().distinct().collect(Collectors.toList());
        return new PageImpl<>(distinctResult, pageable, totalRows);
    }

    private Predicate createPredicate(SearchAndSortCertificateParams params, CriteriaBuilder cb, Root<Certificate> root) {
        return Optional.ofNullable(params.getTags())
                .map(tagName -> searchByTagsName(cb, root, tagName))
                .or(() -> Optional.ofNullable(params.getName())
                        .map(name -> cb.like(root.get(NAME), PERCENT + name + PERCENT)))
                .or(() -> Optional.ofNullable(params.getDescription())
                        .map(description -> cb.like(root.get(DESCRIPTION), PERCENT + description + PERCENT)))
                .orElse(cb.like(root.get(NAME), PERCENT));
    }

    private void createQuery(SearchAndSortCertificateParams params, CriteriaBuilder cb, CriteriaQuery<Certificate> cr, Root<Certificate> root, Predicate predicate) {
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

    private Predicate searchByTagsName(CriteriaBuilder cb, Root<Certificate> root, String tagName) {
        Predicate result;
        String[] tagsName = tagName.split(",");
        if (tagsName.length > 1) {
            result = getPredicateLotsOfTags(cb, root, tagsName);
        } else {
            Join<Certificate, Tag> tagJoin = root.join(TAGS);
            result = cb.equal(tagJoin.get(NAME), tagName);
        }
        return result;
    }

    private Predicate getPredicateLotsOfTags(CriteriaBuilder cb, Root<Certificate> root, String[] tagsName) {
        List<Predicate> predicateList = Arrays.stream(tagsName)
                .map(name -> joinTags(cb, root, name))
                .collect(Collectors.toList());
        Predicate[] predicates = new Predicate[predicateList.size()];
        return cb.and(predicateList.toArray(predicates));
    }

    private Predicate joinTags(CriteriaBuilder cb, Root<Certificate> root, String name) {
        Join<Certificate, Tag> tagsJoin = root.join(TAGS);
        return cb.equal(tagsJoin.get(NAME), name);
    }
}
