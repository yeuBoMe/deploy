package com.computerShop.demo1.service;

import com.computerShop.demo1.domain.*;
import com.computerShop.demo1.domain.dto.ProductCriteriaDTO;
import com.computerShop.demo1.repository.*;
import com.computerShop.demo1.service.send.SendEmailService;
import com.computerShop.demo1.service.specification.ProductSpecs;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserService userService;
    private final SendEmailService sendEmailService;

    public ProductService(ProductRepository productRepository,
                          CartRepository cartRepository,
                          CartDetailRepository cartDetailRepository,
                          OrderRepository orderRepository,
                          OrderDetailRepository orderDetailRepository,
                          UserService userService,
                          SendEmailService sendEmailService) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userService = userService;
        this.sendEmailService = sendEmailService;
    }

    public Product handleSaveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public Optional<Product> getProductById(long id) {
        return this.productRepository.findById(id);
    }

    @Transactional
    public void deleteProductById(long id, HttpSession session) {
        Optional<Product> productOptional = this.productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product productGetById = productOptional.get();

            List<OrderDetail> orderDetails = this.orderDetailRepository.findByProduct(productGetById);
            if (!orderDetails.isEmpty()) {
                throw new IllegalStateException("Deletion failed: Product was in active orders!");
            }

            List<CartDetail> cartDetails = this.cartDetailRepository.findByProduct(productGetById);
            if (!cartDetails.isEmpty()) {
                for (CartDetail cartDetail : cartDetails) {
                    Cart cart = cartDetail.getCart();
                    this.cartDetailRepository.deleteById(cartDetail.getId());

                    if (cart.getSum() > 1) {
                        int sumDecrease = cart.getSum() - 1;
                        cart.setSum(sumDecrease);
                        this.cartRepository.save(cart);
                        session.setAttribute("sum", sumDecrease);
                    } else {
                        this.cartRepository.deleteById(cart.getId());
                        session.setAttribute("sum", 0);
                    }
                }
            }

            this.productRepository.deleteById(id);
        }
    }

    public long countAllProducts() {
        return this.productRepository.count();
    }

    public boolean checkProductExistsWithName(String name) {
        return this.productRepository.existsByName(name);
    }

    public boolean checkProductExistsWithNameAndFactory(String name, String factory) {
        return this.productRepository.existsByNameAndFactory(name, factory);
    }

    // Lấy danh sách factory và số lượng sản phẩm
    public List<Map.Entry<String, Long>> getFactoryCounts() {
        List<Object[]> results = this.productRepository.findFactoryCounts();
        // Chuyển đổi thành List<Map.Entry<String, Long>> và sắp xếp theo factory
        return results.stream()
                .map(result -> Map.entry((String) result[0], (Long) result[1]))
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    public void handleAddProductToCart(HttpSession session,
                                       String userEmailGetFromSession,
                                       long productId,
                                       long quantity) {
        User userGetByEmail = this.userService.getUserByEmail(userEmailGetFromSession);
        if (userGetByEmail != null) {
            Cart cartGetByUser = this.cartRepository.findByUser(userGetByEmail);
            if (cartGetByUser == null) {
                Cart newCart = new Cart();
                newCart.setUser(userGetByEmail);
                newCart.setSum(0);
                cartGetByUser = this.cartRepository.save(newCart);
            }

            Optional<Product> productOptional = this.productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product productGetById = productOptional.get();

                CartDetail oldCartDetail = this.cartDetailRepository.findByCartAndProduct(cartGetByUser, productGetById);
                if (oldCartDetail == null) {
                    CartDetail newCartDetail = new CartDetail();
                    newCartDetail.setCart(cartGetByUser);
                    newCartDetail.setProduct(productGetById);
                    newCartDetail.setPrice(productGetById.getPrice());
                    newCartDetail.setQuantity(quantity);
                    this.cartDetailRepository.save(newCartDetail);

                    // update cart sum
                    int sumIncrease = cartGetByUser.getSum() + 1;
                    cartGetByUser.setSum(sumIncrease);
                    this.cartRepository.save(cartGetByUser);
                    session.setAttribute("sum", sumIncrease);
                } else {
                    oldCartDetail.setQuantity(oldCartDetail.getQuantity() + quantity);
                    this.cartDetailRepository.save(oldCartDetail);
                }
            }
        }
    }

    public void handleDeleteEachProductFromCart(HttpSession session, long cartDetailId) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetailGetById = cartDetailOptional.get();
            Cart currentCart = cartDetailGetById.getCart();

            this.cartDetailRepository.deleteById(cartDetailId);

            if (currentCart.getSum() > 1) {
                int sumDecrease = currentCart.getSum() - 1;
                currentCart.setSum(sumDecrease);
                this.cartRepository.save(currentCart);
                session.setAttribute("sum", sumDecrease);
            } else {
                this.cartRepository.deleteById(currentCart.getId());
                session.setAttribute("sum", 0);
            }
        }
    }

    public void handleDeleteAllProductsFromCart(HttpSession session, String userEmailGetFromSession) {
        User userGetByEmail = this.userService.getUserByEmail(userEmailGetFromSession);
        if (userGetByEmail != null) {
            Cart cartGetByUser = this.cartRepository.findByUser(userGetByEmail);
            if (cartGetByUser != null) {
                List<CartDetail> cartDetails = cartGetByUser.getCartDetails();
                this.cartDetailRepository.deleteAll(cartDetails);
                this.cartRepository.deleteById(cartGetByUser.getId());
                session.setAttribute("sum", 0);
            }
        }
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cartDetailOptional.isPresent()) {
                CartDetail currentCartDetail = cartDetailOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePayment(HttpSession session,
                              User user,
                              String receiverName,
                              String receiverAddress,
                              String receiverPhone,
                              String receiverEmail) {
        Cart cartGetByUser = this.cartRepository.findByUser(user);
        if (cartGetByUser != null) {
            List<CartDetail> cartDetails = cartGetByUser.getCartDetails();
            if (!cartDetails.isEmpty()) {
                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhone);
                order.setStatus("PENDING");

                double totalPrice = 0;
                for (CartDetail cartDetail : cartDetails) {
                    totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
                }

                order.setTotalPrice(totalPrice);
                this.orderRepository.save(order);

                for (CartDetail cartDetail : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cartDetail.getProduct());
                    orderDetail.setPrice(cartDetail.getPrice());
                    orderDetail.setQuantity(cartDetail.getQuantity());
                    this.orderDetailRepository.save(orderDetail);
                }

                // Chuẩn bị chi tiết đơn hàng để gửi email
                StringBuilder orderDetailsHtml = new StringBuilder();
                for (CartDetail cartDetail : cartDetails) {
                    orderDetailsHtml.append(String.format(
                            "<tr><td style='padding: 10px;'>%s</td><td style='padding: 10px;'>%d</td><td style='padding: 10px;'>%,.0f đ</td></tr>",
                            cartDetail.getProduct().getName(),
                            cartDetail.getQuantity(),
                            cartDetail.getPrice() * cartDetail.getQuantity()
                    ));
                }

                // Gửi email đến email mà người dùng nhập
                try {
                    System.out.println("Preparing to send email to: " + receiverEmail);
                    sendEmailService.sendOrderConfirmationEmail(
                            receiverEmail, // Sử dụng email từ form
                            receiverName,
                            receiverAddress,
                            receiverPhone,
                            orderDetailsHtml.toString(),
                            totalPrice
                    );
                    System.out.println("Email sent successfully to: " + receiverEmail);

                } catch (Exception e) {
                    System.err.println("Failed to send email to: " + receiverEmail);
                    e.printStackTrace();
                }

                this.cartDetailRepository.deleteAll(cartDetails);
                this.cartRepository.deleteById(cartGetByUser.getId());
                session.setAttribute("sum", 0);
            }
        }
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    public Page<Product> getProductsWithName(Pageable pageable, String name) {
        return this.productRepository.findAll(ProductSpecs.nameLike(name), pageable);
    }

    public Page<Product> getProductsWithListFactory(Pageable pageable, List<String> factories) {
        return this.productRepository.findAll(ProductSpecs.listFactoryLike(factories), pageable);
    }

    public Page<Product> getProductsWithMinPrice(Pageable pageable, double price) {
        return this.productRepository.findAll(ProductSpecs.priceGreaterThanOrEqual(price), pageable);
    }

    public Page<Product> getProductsWithPriceRange(Pageable pageable, String price) {
        double min = 0;
        Double max = null;

        if (price.equals("duoi-10-trieu")) {
            min = 0;
            max = 10_000_000.0;
        } else if (price.equals("10-den-20-trieu")) {
            min = 10_000_000;
            max = 20_000_000.0;
        } else if (price.equals("20-den-30-trieu")) {
            min = 20_000_000;
            max = 30_000_000.0;
        } else if (price.equals("tren-30-trieu")) {
            min = 30_000_000;
        } else {
            return this.productRepository.findAll(pageable);
        }

        if (max != null) {
            return this.productRepository.findAll(ProductSpecs.priceRange(min, max), pageable);
        } else {
            return this.productRepository.findAll(ProductSpecs.priceGreaterThanOrEqual(min), pageable);
        }
    }

    public Page<Product> getProductsWithArrPrice(Pageable pageable, List<String> prices) {
        Specification<Product> combinedSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.disjunction();

        int count = 0;
        for (String p : prices) {
            double min = 0;
            Double max = null;

            switch (p) {
                case "duoi-10-trieu":
                    min = 0;
                    max = 10_000_000.0;
                    count++;
                    break;
                case "10-den-20-trieu":
                    min = 10_000_000;
                    max = 20_000_000.0;
                    count++;
                    break;
                case "20-den-30-trieu":
                    min = 20_000_000;
                    max = 30_000_000.0;
                    count++;
                    break;
                case "tren-30-trieu":
                    min = 30_000_000;
                    count++;
                    break;
            }

            if (max != null) {
                Specification<Product> rangeSpec = ProductSpecs.priceRange(min, max);
                combinedSpec = combinedSpec.or(rangeSpec);
            } else {
                Specification<Product> minSpec = ProductSpecs.priceGreaterThanOrEqual(min);
                combinedSpec = combinedSpec.or(minSpec);
            }
        }

        if (count == 0) {
            return this.productRepository.findAll(pageable);
        }

        return this.productRepository.findAll(combinedSpec, pageable);
    }

    public Page<Product> getProductsWithSpecs(Pageable pageable, ProductCriteriaDTO productCriteriaDTO) {
        if (productCriteriaDTO.getListFactoryOptional() == null
                && productCriteriaDTO.getListTargetOptional() == null
                && productCriteriaDTO.getListPriceOptional() == null
                && productCriteriaDTO.getSortOptional() == null) {
            return this.productRepository.findAll(pageable);
        }

        Specification<Product> combinedSpec = Specification.where(null);

        if (productCriteriaDTO.getListFactoryOptional().isPresent()) {
            List<String> factories = productCriteriaDTO.getListFactoryOptional().get();
            Specification<Product> currentSpec = ProductSpecs.listFactoryLike(factories);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (productCriteriaDTO.getListTargetOptional().isPresent()) {
            List<String> targets = productCriteriaDTO.getListTargetOptional().get();
            Specification<Product> currentSpec = ProductSpecs.listTargetLike(targets);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (productCriteriaDTO.getListPriceOptional().isPresent()) {
            List<String> prices = productCriteriaDTO.getListPriceOptional().get();
            Specification<Product> currentSpec = this.getProductsWithListPrice(prices);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.productRepository.findAll(combinedSpec, pageable);
    }

    public Specification<Product> getProductsWithListPrice(List<String> prices) {
        Specification<Product> combinedSpec = Specification.where(null);

        for (String p : prices) {
            double min = 0;
            Double max = null;

            switch (p) {
                case "duoi-10-trieu":
                    min = 0;
                    max = 10_000_000.0;
                    break;
                case "10-den-20-trieu":
                    min = 10_000_000.0;
                    max = 20_000_000.0;
                    break;
                case "20-den-30-trieu":
                    min = 20_000_000.0;
                    max = 30_000_000.0;
                    break;
                case "tren-30-trieu":
                    min = 30_000_000.0;
                    break;
            }

            if (max != null) {
                Specification<Product> rangeSpec = ProductSpecs.priceRange(min, max);
                combinedSpec = combinedSpec.or(rangeSpec);
            } else {
                Specification<Product> minSpec = ProductSpecs.priceGreaterThanOrEqual(min);
                combinedSpec = combinedSpec.or(minSpec);
            }
        }

        return combinedSpec;
    }
}