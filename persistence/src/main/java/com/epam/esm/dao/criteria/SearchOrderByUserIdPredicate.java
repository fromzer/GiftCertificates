package com.epam.esm.dao.criteria;

import com.epam.esm.entity.Order;
import com.epam.esm.model.SearchOrderByUserIdParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SearchOrderByUserIdPredicate implements PredicateConstructor<SearchOrderByUserIdParams, Order> {
    private static final String USER_ID = "id";
    private static final String USER = "user";

    @Override
    public Predicate createPredicate(SearchOrderByUserIdParams params, CriteriaBuilder cb, Root<Order> root) {
        return cb.equal(root.get(USER).get(USER_ID), params.getUserId());
    }
}
