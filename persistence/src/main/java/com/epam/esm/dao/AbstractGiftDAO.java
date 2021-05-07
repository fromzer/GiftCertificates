package com.epam.esm.dao;

import com.epam.esm.dao.criteria.PredicateConstructor;
import com.epam.esm.dao.criteria.QueryConstructor;
import com.epam.esm.exception.EntityRetrievalException;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortParams;
import org.springframework.data.domain.Persistable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class AbstractGiftDAO<T extends Persistable<? extends Serializable>> implements GiftDAO<T> {
    private Class<T> clazz;

    @PersistenceContext
    EntityManager entityManager;

    public void setClazz(Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    public EntityManager getEm() {
        return entityManager;
    }

    @Override
    public T findById(Long id) {
        return id == null ? null : getEm().find(clazz, id);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public T createNativeQuery(String query, String param, String value) {
        return (T) getEm().createNativeQuery(query, clazz)
                .setParameter(param, value)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public T createNativeQuery(String query, Map<String, String> params) {
        Query nativeQuery = getEm().createNativeQuery(query, clazz);
        Set<Map.Entry<String, String>> searchParameters = params.entrySet();
        for (Map.Entry<String, String> entry : searchParameters) {
            nativeQuery.setParameter(entry.getKey(), entry.getValue());
        }
        try {
            return (T) nativeQuery.getSingleResult();
        } catch (NoResultException exception) {
            throw new EntityRetrievalException(exception);
        }
    }

    @Override
    public Long create(T entity) {
        getEm().persist(entity);
        return (Long) entity.getId();
    }

    @Override
    public T update(T entity) {
        return getEm().merge(entity);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<T> findAll(Pageable pageable) {
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(clazz);
        Root<T> root = cr.from(clazz);
        cr.select(root);
        Query query = getEm().createQuery(cr);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getSize());
        return query.getResultList();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<T> findEntitiesByParams(SearchAndSortParams params,
                                        Pageable pageable,
                                        PredicateConstructor predicateConstructor,
                                        QueryConstructor queryConstructor) {
        CriteriaBuilder cb = getEm().getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(clazz);
        Root<T> root = cr.from(clazz);
        Predicate predicateSearchParams = predicateConstructor.createPredicate(params, cb, root);
        queryConstructor.createQuery(params, cb, cr, root, predicateSearchParams);
        Query query = getEm().createQuery(cr);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getSize());
        List<T> resultList = query.getResultList();
        return resultList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void delete(T entity) {
        T t = getEm().contains(entity) ? entity : getEm().merge(entity);
        getEm().remove(t); //
    }
}
