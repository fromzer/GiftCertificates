package com.epam.esm.repository;

import com.epam.esm.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    List<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findOrderByIdAndUserId(Long orderId, Long userId);
}
