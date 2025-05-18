package com.computerShop.demo1.controller.client;

import com.computerShop.demo1.domain.Product;
import com.computerShop.demo1.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class ItemController {
    private final ProductService productService;

    public ItemController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public String getItemDetailPage(Model model, @PathVariable long id) {
        Product productGetById = this.productService.getProductById(id).get();
        if (productGetById != null) {
            model.addAttribute("product", productGetById);
            model.addAttribute("id", id);
            model.addAttribute("factoryCounts", this.productService.getFactoryCounts());
        }
        return "client/product/item-detail";
    }

    @PostMapping("/add-product-from-home-to-cart/{id}")
    public String handleAddProductFromHomeToCartRequest(HttpServletRequest request, @PathVariable long id) {
        HttpSession session = request.getSession(false);
        String userEmailGetFromSession = (String) session.getAttribute("email");
        this.productService.handleAddProductToCart(session, userEmailGetFromSession, id, 1);
        return "redirect:/";
    }

    @PostMapping("/add-product-detail-to-cart")
    public String handleAddProductFromDetailToCartRequest(HttpServletRequest request,
                                                          @RequestParam("id") long id,
                                                          @RequestParam("quantity") long quantity) {
        HttpSession session = request.getSession(false);
        String userEmailGetFromSession = (String) session.getAttribute("email");
        this.productService.handleAddProductToCart(session, userEmailGetFromSession, id, quantity);
        return "redirect:/product/" + id;
    }

    @PostMapping("/add-product-from-list-to-cart/{id}")
    public String handleAddProductFromListToCartRequest(HttpServletRequest request,
                                                        @PathVariable long id,
                                                        @RequestParam(value = "page", required = false) Optional<String> pageOptional) {

        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        HttpSession session = request.getSession(false);
        String userEmailGetFromSession = (String) session.getAttribute("email");
        this.productService.handleAddProductToCart(session, userEmailGetFromSession, id, 1);

        // Lấy referer URL (URL gốc của trang /products)
        String referer = request.getHeader("Referer");
        String redirectUrl = "/products?page=" + page;

        if (referer != null) {
            try {
                // Tách query string từ referer URL
                String queryString = "";
                int queryIndex = referer.indexOf("?");
                if (queryIndex != -1) {
                    queryString = referer.substring(queryIndex + 1);
                }

                // Loại bỏ page cũ và giữ các tham số còn lại
                if (!queryString.isEmpty()) {
                    String[] params = queryString.split("&");
                    StringBuilder cleanedQs = new StringBuilder();
                    for (String param : params) {
                        if (!param.startsWith("page=")) {
                            if (cleanedQs.length() > 0) {
                                cleanedQs.append("&");
                            }
                            cleanedQs.append(param);
                        }
                    }
                    // Thêm các tham số còn lại vào redirect URL
                    if (cleanedQs.length() > 0) {
                        redirectUrl += "&" + URLDecoder.decode(cleanedQs.toString(), StandardCharsets.UTF_8);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Referer URL: " + referer); // Debug
        System.out.println("Redirect URL: " + redirectUrl); // Debug
        return "redirect:" + redirectUrl;
    }
}
