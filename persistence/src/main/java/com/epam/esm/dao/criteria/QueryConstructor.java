package com.epam.esm.dao.criteria;

import com.epam.esm.model.SearchAndSortParams;
import org.springframework.data.domain.Persistable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;

/**
 * Base interface class for a modified query
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface QueryConstructor<T extends SearchAndSortParams, K extends Persistable<? extends Serializable>>{
    void createQuery(T params, CriteriaBuilder cb, CriteriaQuery<K> cr, Root<K> root, Predicate predicate);
}
