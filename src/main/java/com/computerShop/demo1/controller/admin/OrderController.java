package com.computerShop.demo1.controller.admin;

import com.computerShop.demo1.domain.Order;
import com.computerShop.demo1.domain.OrderDetail;
import com.computerShop.demo1.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {
    
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/order")
    public String getListOrdersPage(Model model,
                                   @RequestParam(value = "page", required = false) Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pageable pageable = PageRequest.of(page - 1, 3);
        Page<Order> pageHavOrders = this.orderService.getAllOrders(pageable);
        List<Order> orders = pageHavOrders.getContent();
        int allPages = pageHavOrders.getTotalPages();

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("allPages", allPages);

        return "admin/order/list-orders";
    }

    @GetMapping("/admin/order/{id}")
    public String getDetailOrderPage(Model model, @PathVariable long id) {
        Order orderGetById = this.orderService.getOrderById(id).get();
        if (orderGetById != null) {
            List<OrderDetail> orderDetails = orderGetById.getOrderDetails();
            model.addAttribute("orderDetails", orderDetails);
            model.addAttribute("id", id);
        }
        return "admin/order/detail-order";
    }

    @GetMapping("/admin/order/update/{id}")
    public String getUpdateOrderPage(Model model, @PathVariable long id) {
        Order orderGetById = this.orderService.getOrderById(id).get();
        if (orderGetById != null) {
            model.addAttribute("updateOrder", orderGetById);
            model.addAttribute("id", id);
        }
        return "admin/order/update-order";
    }

    @PostMapping("/admin/order/update")
    public String handleUpdateOrderRequest(@ModelAttribute("updateOrder") Order order) {
        Order currentOrder = this.orderService.getOrderById(order.getId()).get();
        if (currentOrder != null) {
            currentOrder.setStatus(order.getStatus());
            this.orderService.handleSaveOrder(currentOrder);
        }
        return "redirect:/admin/order";
    }

    @GetMapping("admin/order/delete/{id}")
    public String getDeleteOrderPage(Model model, @PathVariable long id) {
        Order orderGetById = this.orderService.getOrderById(id).get();
        if (orderGetById != null) {
            model.addAttribute("deleteOrder", orderGetById);
            model.addAttribute("id", id);
        }
        return "admin/order/delete-order";
    }

    @PostMapping("/admin/order/delete")
    public String handleDeleteOrderRequest(@ModelAttribute("deleteOrder") Order order) {
        this.orderService.deleteOrderById(order.getId());
        return "redirect:/admin/order";
    }
}
