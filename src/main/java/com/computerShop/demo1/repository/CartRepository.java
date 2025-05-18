package com.computerShop.demo1.repository;

import com.computerShop.demo1.domain.Cart;
import com.computerShop.demo1.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart save(Cart cart);

    Cart findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.id = :id")
    void deleteById(long id);
}
