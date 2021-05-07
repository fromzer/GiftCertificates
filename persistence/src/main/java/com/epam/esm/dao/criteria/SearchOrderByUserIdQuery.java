package com.epam.esm.dao.criteria;

import com.epam.esm.entity.Order;
import com.epam.esm.model.SearchOrderByUserIdParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SearchOrderByUserIdQuery implements QueryConstructor<SearchOrderByUserIdParams, Order> {
    @Override
    public void createQuery(SearchOrderByUserIdParams params, CriteriaBuilder cb, CriteriaQuery<Order> cr, Root<Order> root, Predicate predicate) {
        cr.select(root).where(cb.and(predicate));
    }
}
