package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.model.Pageable;
import com.epam.esm.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    private final GiftDaoBean<User> giftDao;

    @Autowired
    public UserDaoImpl(GiftDaoBean giftDao) {
        this.giftDao = giftDao;
        giftDao.setClazz(User.class);
    }

    @Override
    public User findById(Long id) {
        return giftDao.findById(id);
    }

    @Override
    public List<User> findAll(Pageable pageable) {
        return giftDao.findAll(pageable);
    }
}
