package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {
    String selectMostPopularUserTag = "SELECT id, name from (" +
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

    Tag findTagByName(String name);

    @Query(value = selectMostPopularUserTag, nativeQuery = true)
    Tag getMostPopularUserTag(@Param("userId") Long userId);
}
