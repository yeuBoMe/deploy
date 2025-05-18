package com.computerShop.demo1.repository;

import com.computerShop.demo1.domain.Cart;
import com.computerShop.demo1.domain.CartDetail;
import com.computerShop.demo1.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

    CartDetail save(CartDetail cartDetail);

    Optional<CartDetail> findById(long id);

    void deleteById(long id);

    CartDetail findByCartAndProduct(Cart cart, Product product);

    List<CartDetail> findByProduct(Product product);
}
