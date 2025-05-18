package com.computerShop.demo1.controller.client;

import com.computerShop.demo1.domain.Order;
import com.computerShop.demo1.domain.Product;
import com.computerShop.demo1.domain.Product_;
import com.computerShop.demo1.domain.User;
import com.computerShop.demo1.domain.dto.ProductCriteriaDTO;
import com.computerShop.demo1.service.OrderService;
import com.computerShop.demo1.service.ProductService;
import com.computerShop.demo1.service.UploadService;
import com.computerShop.demo1.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class HomePageController {
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final UploadService uploadService;

    public HomePageController(ProductService productService,
            OrderService orderService,
            UserService userService,
            UploadService uploadService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.uploadService = uploadService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Product> pageHavProducts = this.productService.getAllProducts(pageable);
        List<Product> products = pageHavProducts.getContent();
        model.addAttribute("products", products);
        return "client/homepage/home";
    }

    @GetMapping("/products")
    public String getAllProductsPage(Model model,
            HttpServletRequest request,
            @RequestParam(value = "page", required = false) Optional<String> pageOptional,
            @RequestParam(value = "name", required = false) Optional<String> nameOptional,
            @RequestParam(value = "factory", required = false) Optional<String> factoryOptional,
            @RequestParam(value = "target", required = false) Optional<String> targetOptional,
            @RequestParam(value = "price", required = false) Optional<String> priceOptional,
            @RequestParam(value = "sort", required = false) Optional<String> sortOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProductCriteriaDTO productCriteriaDTO = new ProductCriteriaDTO();
        productCriteriaDTO.setPageOptional(pageOptional);
        productCriteriaDTO.setNameOptional(nameOptional);

        if (factoryOptional.isPresent()) {
            List<String> factories = Arrays.asList(factoryOptional.get().split(","));
            productCriteriaDTO.setListFactoryOptional(Optional.of(factories));
        } else {
            productCriteriaDTO.setListFactoryOptional(Optional.empty());
        }

        if (targetOptional.isPresent()) {
            List<String> targets = Arrays.asList(targetOptional.get().split(","));
            productCriteriaDTO.setListTargetOptional(Optional.of(targets));
        } else {
            productCriteriaDTO.setListTargetOptional(Optional.empty());
        }

        if (priceOptional.isPresent()) {
            List<String> prices = Arrays.asList(priceOptional.get().split(","));
            productCriteriaDTO.setListPriceOptional(Optional.of(prices));
        } else {
            productCriteriaDTO.setListPriceOptional(Optional.empty());
        }

        Pageable pageable;
        if (sortOptional.isPresent()) {
            String sort = sortOptional.get();
            if (sort.equals("gia-tang-dan")) {
                pageable = PageRequest.of(page - 1, 5, Sort.by(Product_.PRICE).ascending());
                productCriteriaDTO.setSortOptional(Optional.of(sort));
            } else if (sort.equals("gia-giam-dan")) {
                pageable = PageRequest.of(page - 1, 5, Sort.by(Product_.PRICE).descending());
                productCriteriaDTO.setSortOptional(Optional.of(sort));
            } else {
                pageable = PageRequest.of(page - 1, 20);
                productCriteriaDTO.setSortOptional(Optional.empty());
            }
        } else {
            pageable = PageRequest.of(page - 1, 20);
            productCriteriaDTO.setSortOptional(Optional.empty());
        }

        String qs = request.getQueryString();
        if (qs != null && !qs.isBlank()) {
            qs = qs.replace("page=" + page, "");
        }

        Page<Product> pageHavProducts = this.productService.getProductsWithSpecs(pageable, productCriteriaDTO);
        List<Product> products = pageHavProducts.getContent();
        int allPages = pageHavProducts.getTotalPages();

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("allPages", allPages);
        model.addAttribute("queryString", qs);

        return "client/product/all-products";
    }

    @GetMapping("/order-history")
    public String getOrderHistoryPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        long userIdGetFromSession = (long) session.getAttribute("id");

        User user = new User();
        user.setId(userIdGetFromSession);

        List<Order> orders = this.orderService.getListOrderByUser(user);
        model.addAttribute("orders", orders);

        return "client/cart/order-history";
    }

    @GetMapping("/profile")
    public String getProfilePage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String userEmailGetFromSession = (String) session.getAttribute("email");

        User userGetByEmail = this.userService.getUserByEmail(userEmailGetFromSession);
        model.addAttribute("updateProfile", userGetByEmail);

        return "client/user/profile";
    }

    @PostMapping("/profile")
    public String handleUpdateProfileRequest(@ModelAttribute("updateProfile") @Valid User user,
            BindingResult updateProfileBindingResult,
            @RequestParam("userAvatar") MultipartFile multipartFile,
            Model model) {
        List<FieldError> errors = updateProfileBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>>>" + error.getField() + "-" + error.getDefaultMessage());
        }

        User currentUser = this.userService.getUserById(user.getId()).get();
        String userAvatar = this.uploadService.handleUploadUserAvatar(multipartFile);

        if (updateProfileBindingResult.hasErrors()) {
            model.addAttribute("updateProfile", user);
            user.setAvatar(currentUser.getAvatar());
            return "client/user/profile";
        }

        if (currentUser != null) {
            currentUser.setFullName(user.getFullName());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setAddress(user.getAddress());

            if (!multipartFile.isEmpty()) {
                currentUser.setAvatar(userAvatar);
            }

            this.userService.handleSaveUser(currentUser);
        }

        return "redirect:/profile";
    }
}
