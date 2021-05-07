package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftDAO;
import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.model.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class GiftTagDAOImpl implements GiftTagDao {
    private static final String SQL_SELECT_MOST_POPULAR_USER_TAG = "SELECT id, name from (" +
            "SELECT t.id, t.name, count(*) as quantity, sum(cost) as sum_cost, user_id " +
            "from orders " +
            "JOIN order_certificate oc on orders.id = oc.order_id " +
            "JOIN gift_certificate gc on gc.id = oc.certificate_id " +
            "JOIN gift_certificate_tag gct on gc.id = gct.gift_certificate_id " +
            "JOIN tag t on t.id = gct.tag_id " +
            "WHERE user_id = :userId " +
            "GROUP BY t.name " +
            "ORDER BY quantity DESC, sum_cost DESC " +
            "LIMIT 1 " +
            ") as tag;";
    private static final String SQL_SELECT_FIND_NAME = "SELECT id, name FROM tag WHERE name=:name";
    private static final String NAME = "name";
    private static final String USER_ID = "userId";

    private final GiftDAO<Tag> giftDao;

    @Autowired
    public GiftTagDAOImpl(GiftDaoBean giftDao) {
        this.giftDao = giftDao;
        giftDao.setClazz(Tag.class);
    }

    @Override
    @Transactional
    public Long create(Tag tag) {
        return giftDao.create(tag);
    }

    @Override
    public void delete(Tag tag) {
        giftDao.delete(tag);
    }

    @Override
    public List<Tag> findAll(Pageable pageable) {
        return giftDao.findAll(pageable);
    }

    @Override
    public Tag findById(Long id) {
        return giftDao.findById(id);
    }

    @Override
    public Tag findByName(String name) {
        return giftDao.createNativeQuery(SQL_SELECT_FIND_NAME, NAME, name);
    }

    @Override
    public Tag findMostPopularUserTag(Long userId) {
        return giftDao.createNativeQuery(SQL_SELECT_MOST_POPULAR_USER_TAG, USER_ID, String.valueOf(userId));
    }
}
