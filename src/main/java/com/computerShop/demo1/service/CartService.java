package com.computerShop.demo1.service;

import com.computerShop.demo1.domain.Cart;
import com.computerShop.demo1.domain.User;
import com.computerShop.demo1.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart getCartByUser(User user) {
        return this.cartRepository.findByUser(user);
    }
}
