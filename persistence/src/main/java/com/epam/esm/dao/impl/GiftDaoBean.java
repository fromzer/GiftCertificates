package com.epam.esm.dao.impl;

import com.epam.esm.dao.AbstractGiftDAO;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
@Scope("prototype")
public class GiftDaoBean<T extends Persistable<? extends Serializable>>
        extends AbstractGiftDAO<T> {
}
