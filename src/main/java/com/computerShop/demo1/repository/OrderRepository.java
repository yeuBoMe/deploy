package com.computerShop.demo1.repository;

import com.computerShop.demo1.domain.Order;
import com.computerShop.demo1.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order save(Order order);

    Page<Order> findAll(Pageable pageable);

    Optional<Order> findById(long id);

    void deleteById(long id);

    long count();

    List<Order> findByUser(User user);
}
