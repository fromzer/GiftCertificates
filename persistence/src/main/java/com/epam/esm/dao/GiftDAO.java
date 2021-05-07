package com.epam.esm.dao;

import com.epam.esm.dao.criteria.PredicateConstructor;
import com.epam.esm.dao.criteria.QueryConstructor;
import com.epam.esm.model.Pageable;
import com.epam.esm.model.SearchAndSortParams;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Base interface class for Gift DAO bean
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface GiftDAO<T extends Persistable<? extends Serializable>> extends Serializable {
    /**
     * Create entry in DB
     *
     * @param entity an entity of business model
     * @return new entity id if add DB table entry
     */
    Long create(T entity);

    /**
     * Update entry in DB
     *
     * @param entity an entity of business model
     * @return entity if entry updated
     */
    T update(T entity);

    /**
     * Find entry by entity id in table
     *
     * @param id entity id
     * @return entity
     */
    T findById(Long id);

    /**
     * Create native query
     *
     * @param query sql query
     * @param param named param
     * @param value param value
     * @return entity
     */
    T createNativeQuery(String query, String param, String value);

    /**
     * Create native query
     *
     * @param query  sql query
     * @param params (named param, param value) map
     * @return entity
     */
    T createNativeQuery(String query, Map<String, String> params);

    /**
     * Find all entries in table
     *
     * @param pageable pagination
     * @return List of entities
     */
    List<T> findAll(Pageable pageable);

    /**
     * Find entity by params
     *
     * @param params               the search and sort options
     * @param pageable             pagination
     * @param predicateConstructor simple or compound predicate.
     * @param queryConstructor     modified query
     * @return list of entities
     */
    List<T> findEntitiesByParams(SearchAndSortParams params, Pageable pageable,
                                 PredicateConstructor predicateConstructor, QueryConstructor queryConstructor);

    /**
     * Delete entry in table
     *
     * @param entity entity to delete
     */
    void delete(T entity);
}
