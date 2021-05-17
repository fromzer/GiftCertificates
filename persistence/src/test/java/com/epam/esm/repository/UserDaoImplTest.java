package com.epam.esm.repository;

import com.epam.esm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql({"classpath:create_tables.sql"})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class UserDaoImplTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    @Mock
    private Pageable pageable;

    private User user = User.builder()
            .login("test")
            .firstName("Test")
            .lastName("Testov")
            .password("test")
            .build();

    @BeforeEach
    public void create() {
        entityManager.getEntityManager().persist(user);
        entityManager.flush();
    }

    @Test
    void shouldFindUserById() {
        User excepted = userRepository.findById(1L).get();
        assertNotNull(excepted);
        assertEquals(excepted.getFirstName(), "Test");
    }

    @Test
    void shouldFindAllUsers() {
        User actual = userRepository.findById(1L).get();
        List<User> expected = userRepository.findAll(pageable).getContent();
        assertEquals(expected.get(0), actual);
    }
}