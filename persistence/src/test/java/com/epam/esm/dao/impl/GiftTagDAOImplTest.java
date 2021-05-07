package com.epam.esm.dao.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.exception.CreateEntityException;
import com.epam.esm.exception.DeleteEntityException;
import com.epam.esm.exception.EntityRetrievalException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
@Transactional
class GiftTagDAOImplTest {
    @Autowired
    private GiftTagDAOImpl giftTagDAOImpl;
    @Autowired
    private UserDaoImpl userDao;

    private Pageable pageable;
    private Tag tag;

    @BeforeEach
    public void create() {
        pageable = new Pageable(1, 10);
        tag = Tag.builder()
                .name("Dodge")
                .build();
    }

    @Test
    void shouldFindMostPopularTag() throws CreateEntityException, EntityRetrievalException {
        User user = userDao.findById(2l);
        Tag mostPopularUserTag = giftTagDAOImpl.findMostPopularUserTag(user.getId());
        assertNotNull(mostPopularUserTag);
    }


    @Test
    void shouldFindTagById() throws EntityRetrievalException {
        Tag dto = giftTagDAOImpl.findById(9L);
        assertEquals(dto.getName(), "WoW");
    }

    @Test
    void shouldNotFindTagById() throws EntityRetrievalException {
        assertNull(giftTagDAOImpl.findById(66L));
    }

    @Test
    void shouldDeleteTag() throws DeleteEntityException, EntityRetrievalException {
        Tag tag = Tag.builder()
                .id(13L)
                .build();
        giftTagDAOImpl.delete(tag);
        assertNull(giftTagDAOImpl.findById(tag.getId()));
    }

    @Test
    void shouldFindAllTags() throws EntityRetrievalException {
        Tag tag = giftTagDAOImpl.findById(2L);
        List<Tag> tags = giftTagDAOImpl.findAll(pageable);
        assertEquals(tags.get(0), tag);
    }

    @Test
    void shouldFindTagByName() throws EntityRetrievalException {
        Tag actual = giftTagDAOImpl.findByName("SportMaster");
        assertEquals(actual.getName(), "SportMaster");
    }
}