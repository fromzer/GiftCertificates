package com.epam.esm.dao;

import com.epam.esm.entity.Tag;
import com.epam.esm.model.Pageable;

import java.util.List;

/**
 * Base interface class for GiftTagDAO
 *
 * @author Egor Miheev
 * @version 1.0.0
 */
public interface GiftTagDao {
    /**
     * Create tag in DB
     *
     * @param tag a tag of business model
     * @return entry id
     */
    Long create(Tag tag);

    /**
     * Find tag by id in DB
     *
     * @param id tag id
     * @return tag
     */
    Tag findById(Long id);

    /**
     * Find tag by name in DB
     *
     * @param name tag name
     * @return tag
     */
    Tag findByName(String name);

    /**
     * Find the most popular user tag
     *
     * @param userId user id
     * @return tag
     */
    Tag findMostPopularUserTag(Long userId);

    /**
     * Find all tags in DB
     *
     * @param pageable pagination
     * @return list of tags
     */
    List<Tag> findAll(Pageable pageable);

    /**
     * Delete tag in DB
     *
     * @param tag the tag
     */
    void delete(Tag tag);
}
