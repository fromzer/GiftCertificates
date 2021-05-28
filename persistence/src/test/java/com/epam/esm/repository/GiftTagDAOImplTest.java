package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DeleteEntityException;
import com.epam.esm.exception.EntityRetrievalException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql({"classpath:create_tables.sql"})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class GiftTagDAOImplTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;
    @Mock
    private Pageable pageable;
    private Tag tag;

    @BeforeEach
    public void create() {
        tag = Tag.builder()
                .name("Dodge")
                .build();
        entityManager.getEntityManager().persist(tag);
        entityManager.flush();
    }


    @Test
    void shouldFindTagById() throws EntityRetrievalException {
        Tag dto = tagRepository.findById(1L).get();
        assertEquals(dto.getName(), "Dodge");
    }

    @Test
    void shouldNotFindTagById() throws EntityRetrievalException {
        assertEquals(tagRepository.findById(66L), Optional.empty());
    }

    @Test
    void shouldDeleteTag() throws DeleteEntityException, EntityRetrievalException {
        tagRepository.delete(tag);
        assertEquals(tagRepository.findById(tag.getId()), Optional.empty());
    }

    @Test
    void shouldFindAllTags() throws EntityRetrievalException {
        Tag tag = tagRepository.findById(1L).get();
        List<Tag> tags = tagRepository.findAll(pageable).getContent();
        assertEquals(tags.get(0), tag);
    }

    @Test
    void shouldFindTagByName() throws EntityRetrievalException {
        Tag actual = tagRepository.findTagByName("Dodge");
        assertEquals(actual.getName(), "Dodge");
    }
}