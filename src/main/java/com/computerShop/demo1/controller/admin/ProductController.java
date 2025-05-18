package com.computerShop.demo1.controller.admin;

import com.computerShop.demo1.domain.Product;
import com.computerShop.demo1.service.ProductService;
import com.computerShop.demo1.service.UploadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/product")
    public String getListProductsPage(Model model,
                                     @RequestParam(value = "page", required = false) Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {

            }
        } catch (Exception e) {

        }

        Pageable pageable = PageRequest.of(page - 1, 4);
        Page<Product> pageHavProducts = this.productService.getAllProducts(pageable);
        List<Product> products = pageHavProducts.getContent();
        int allPages = pageHavProducts.getTotalPages();

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("allPages", allPages);

        return "admin/product/list-products";
    }

    @GetMapping("/admin/product/{id}")
    public String getDetailProductPage(Model model, @PathVariable long id) {
        Product productGetById = this.productService.getProductById(id).get();
        if (productGetById != null) {
            model.addAttribute("product", productGetById);
            model.addAttribute("id", id);
        }
        return "admin/product/detail-product";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("createProduct", new Product());
        return "admin/product/create-product";
    }

    @PostMapping("/admin/product/create")
    public String handleCreateProductRequest(@ModelAttribute("createProduct") @Valid Product product,
                                             BindingResult createProductBindingResult,
                                             @RequestParam("productImage") MultipartFile multipartFile,
                                             Model model) {
        List<FieldError> errors = createProductBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>>>" + error.getField() + "-" + error.getDefaultMessage());
        }

        // check product exists with name
        if (this.productService.checkProductExistsWithName(product.getName())) {
            createProductBindingResult.addError(
                    new FieldError(
                            "createProduct",
                            "name",
                            "Sản phẩm có tên " + product.getName() + " đã tồn tại!"
                    )
            );
        }

        // check product exists with 2 criteria
        if (this.productService.checkProductExistsWithNameAndFactory(product.getName(), product.getFactory())) {
            createProductBindingResult.addError(
                    new FieldError(
                            "createProduct",
                            "name",
                            "Sản phẩm có tên " + product.getName() + ", hãng " + product.getFactory()
                                    + " đã tồn tại!"
                    )
            );
        }

        if (createProductBindingResult.hasErrors()) {
            model.addAttribute("createProduct", product);
            return "admin/product/create-product";
        }

        String image = this.uploadService.handleUploadProductImage(multipartFile);
        product.setImage(image);

        this.productService.handleSaveProduct(product);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/update/{id}")
    public String getUpdateProductPage(Model model, @PathVariable long id) {
        Product productGetById = this.productService.getProductById(id).get();
        if (productGetById != null) {
            model.addAttribute("updateProduct", productGetById);
            model.addAttribute("id", id);
        }
        return "admin/product/update-product";
    }

    @PostMapping("/admin/product/update")
    public String handleUpdateProductRequest(@ModelAttribute("updateProduct") @Valid Product product,
                                             BindingResult updateProductBindingResult,
                                             @RequestParam("productImage") MultipartFile multipartFile,
                                             Model model) {
        List<FieldError> errors = updateProductBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>>>" + error.getField() + "-" + error.getDefaultMessage());
        }

        Product currentProduct = this.productService.getProductById(product.getId()).get();
        String image = this.uploadService.handleUploadProductImage(multipartFile);

        // check product exists with name
        if (this.productService.checkProductExistsWithName(product.getName())) {
            updateProductBindingResult.addError(
                    new FieldError(
                            "updateProduct",
                            "name",
                            "Sản phẩm có tên " + product.getName() + " đã tồn tại!"
                    )
            );
        }

        // check product exists with 2 criteria
        if (this.productService.checkProductExistsWithNameAndFactory(product.getName(), product.getFactory())) {
            updateProductBindingResult.addError(
                    new FieldError(
                            "updateProduct",
                            "name",
                            "Sản phẩm có tên " + product.getName() + ", hãng " + product.getFactory()
                                    + " đã tồn tại!"
                    )
            );
        }

        if (updateProductBindingResult.hasErrors()) {
            model.addAttribute("updateProduct", product);
            product.setImage(currentProduct.getImage());
            return "admin/product/update-product";
        }

        if (currentProduct != null) {
            currentProduct.setName(product.getName());
            currentProduct.setPrice(product.getPrice());
            currentProduct.setTarget(product.getTarget());
            currentProduct.setFactory(product.getFactory());
            currentProduct.setShortDesc(product.getShortDesc());
            currentProduct.setDetailDesc(product.getDetailDesc());

            if (!multipartFile.isEmpty()) {
                currentProduct.setImage(image);
            }

            this.productService.handleSaveProduct(currentProduct);
        }

        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String getDeleteProductPage(Model model, @PathVariable long id) {
        Product productGetById = this.productService.getProductById(id).get();
        if (productGetById != null) {
            model.addAttribute("deleteProduct", productGetById);
            model.addAttribute("id", id);
        }
        return "admin/product/delete-product";
    }

    @PostMapping("/admin/product/delete")
    public String handleDeleteProductRequest(@ModelAttribute("deleteProduct") Product product,
                                             HttpServletRequest request,
                                             Model model) {
        HttpSession session = request.getSession(false);
        try {
            this.productService.deleteProductById(product.getId(), session);
            return "redirect:/admin/product";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("deleteProduct", product);
            model.addAttribute("id", product.getId());
            return "admin/product/delete-product";
        }
    }
}
