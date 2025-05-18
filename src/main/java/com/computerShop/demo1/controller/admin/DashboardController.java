package com.computerShop.demo1.controller.admin;

import com.computerShop.demo1.service.OrderService;
import com.computerShop.demo1.service.ProductService;
import com.computerShop.demo1.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    public DashboardController(UserService userService,
                               ProductService productService,
                               OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String getDashboardPage(Model model) {
        long numberOfUsers = this.userService.countAllUsers();
        long numberOfProducts = this.productService.countAllProducts();
        long numberOfOrders = this.orderService.countAllOrders();

        model.addAttribute("countUser", numberOfUsers);
        model.addAttribute("countProduct", numberOfProducts);
        model.addAttribute("countOrder", numberOfOrders);

        return "admin/dashboard/dashboard";
    }
}
