package com.epam.esm.dao.impl;

import com.epam.esm.entity.User;
import com.epam.esm.model.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
@Transactional
class UserDaoImplTest {
    @Autowired
    private UserDaoImpl userDao;
    private Pageable pageable;

    @BeforeEach
    public void create() {
        pageable = new Pageable(1, 10);
    }

    @Test
    void shouldFindUserById() {
        User excepted = userDao.findById(2L);
        assertNotNull(excepted);
        assertEquals(excepted.getFirstName(), "Alex");
    }

    @Test
    void shouldFindAllUsers() {
        User actual = userDao.findById(2L);
        List<User> expected = userDao.findAll(pageable);
        assertEquals(expected.get(0), actual);
    }
}