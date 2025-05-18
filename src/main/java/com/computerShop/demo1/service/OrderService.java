package com.computerShop.demo1.service;

import com.computerShop.demo1.domain.Order;
import com.computerShop.demo1.domain.OrderDetail;
import com.computerShop.demo1.domain.User;
import com.computerShop.demo1.repository.OrderDetailRepository;
import com.computerShop.demo1.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Optional<Order> getOrderById(long id) {
        return this.orderRepository.findById(id);
    }

    public Order handleSaveOrder(Order order) {
        return this.orderRepository.save(order);
    }

    public long countAllOrders() {
        return this.orderRepository.count();
    }

    public List<Order> getListOrderByUser(User user) {
        return this.orderRepository.findByUser(user);
    }

    public void deleteOrderById(long id) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order orderGetById = orderOptional.get();
            List<OrderDetail> orderDetails = orderGetById.getOrderDetails();
            this.orderDetailRepository.deleteAll(orderDetails);
            this.orderRepository.delete(orderGetById);
        }
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return this.orderRepository.findAll(pageable);
    }
}
