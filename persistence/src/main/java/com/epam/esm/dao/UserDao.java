package com.epam.esm.dao;

import com.epam.esm.entity.User;
import com.epam.esm.model.Pageable;

import java.util.List;

/**
 * Base interface class for UserDAO
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface UserDao {

    /**
     * Find user by id in DB
     *
     * @param id user id
     * @return user
     */
    User findById(Long id);

    /**
     * Find all users in DB
     *
     * @param pageable pagination
     * @return list of users
     */
    List<User> findAll(Pageable pageable);
}
