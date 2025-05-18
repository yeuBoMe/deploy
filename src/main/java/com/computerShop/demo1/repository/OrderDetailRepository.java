package com.computerShop.demo1.repository;

import com.computerShop.demo1.domain.Order;
import com.computerShop.demo1.domain.OrderDetail;
import com.computerShop.demo1.domain.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAll();

    OrderDetail save(OrderDetail orderDetail);

    void deleteById(long id);

    List<OrderDetail> findByProduct(Product product);
}
