package com.computerShop.demo1.controller.client;

import com.computerShop.demo1.domain.Cart;
import com.computerShop.demo1.domain.CartDetail;
import com.computerShop.demo1.domain.User;
import com.computerShop.demo1.service.CartService;
import com.computerShop.demo1.service.ProductService;
import com.computerShop.demo1.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private final ProductService productService;
    private final CartService cartService;

    public CartController(ProductService productService,
                          CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String getCartDetailPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        long userIdGetFromSession = (long) session.getAttribute("id");

        User user = new User();
        user.setId(userIdGetFromSession);

        Cart cartGetByUser = this.cartService.getCartByUser(user);
        List<CartDetail> cartDetails = cartGetByUser != null ? cartGetByUser.getCartDetails()
                                                             : new ArrayList<CartDetail>();

        double totalPrice = 0;
        for (CartDetail cartDetail : cartDetails) {
            totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
        }

        model.addAttribute("cart", cartGetByUser);
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);

        return "client/cart/cart-detail";
    }

    @PostMapping("/delete-cart/{id}")
    public String handleDeleteProductFromCartRequest(HttpServletRequest request, @PathVariable long id) {
        HttpSession session = request.getSession(false);
        this.productService.handleDeleteEachProductFromCart(session, id);
        return "redirect:/cart";
    }

    @PostMapping("/clear-cart")
    public String handleClearCartDetailRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userEmailGetFromSession = (String) session.getAttribute("email");
        this.productService.handleDeleteAllProductsFromCart(session, userEmailGetFromSession);
        return "redirect:/cart";
    }

    @PostMapping("/confirm-checkout")
    public String handleCheckoutBeforePaymentRequest(@ModelAttribute("cart") Cart cart) {
        List<CartDetail> cartDetails = cart != null ? cart.getCartDetails()
                                                    : new ArrayList<CartDetail>();
        this.productService.handleUpdateCartBeforeCheckout(cartDetails);
        return "redirect:/checkout";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        long userIdGetFromSession = (long) session.getAttribute("id");

        User user = new User();
        user.setId(userIdGetFromSession);

        Cart cartGetByUser = this.cartService.getCartByUser(user);
        List<CartDetail> cartDetails = cartGetByUser != null ? cartGetByUser.getCartDetails()
                                                             : new ArrayList<CartDetail>();

        double totalPrice = 0;
        for (CartDetail cartDetail : cartDetails) {
            totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
        }

        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("userPay", new User());

        return "client/cart/checkout";
    }

    @PostMapping("/place-order")
    public String handlePlaceOrderRequest(HttpServletRequest request,
                                          @ModelAttribute("userPay") @Valid User userForm,
                                          BindingResult userPayBindingResult,
                                          Model model) {
        List<FieldError> errors = userPayBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>>>" + error.getField() + "-" + error.getDefaultMessage());
        }

        HttpSession session = request.getSession(false);
        long userIdGetFromSession = (long) session.getAttribute("id");

        User user = new User();
        user.setId(userIdGetFromSession);

        if (userPayBindingResult.hasErrors()) {
            Cart cartGetByUser = this.cartService.getCartByUser(user);
            List<CartDetail> cartDetails = cartGetByUser != null ? cartGetByUser.getCartDetails()
                                                                 : new ArrayList<CartDetail>();

            double totalPrice = 0;
            for (CartDetail cartDetail : cartDetails) {
                totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
            }

            model.addAttribute("userPay", userForm);
            model.addAttribute("cartDetails", cartDetails);
            model.addAttribute("totalPrice", totalPrice);
            return "client/cart/checkout";
        }

        this.productService.handlePayment(session, user, userForm.getFullName(), userForm.getAddress(),
                userForm.getPhoneNumber(), userForm.getEmail());
        return "redirect:/thank-you";
    }

    @GetMapping("/thank-you")
    public String getThankYouPage() {
        return "client/cart/thanks";
    }
}