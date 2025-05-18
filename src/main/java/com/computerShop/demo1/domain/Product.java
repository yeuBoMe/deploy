package com.computerShop.demo1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;

@Entity
@Table(name = "products")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(unique = true)
    @Size(min = 5, message = "Tên sản phẩm tối thiểu 5 kí tự!")
    @Pattern(
            regexp = "^(?=.*[\\p{L}])[\\p{L}0-9 ]+$",
            message = "Tên sản phẩm phải chứa ít nhất một chữ cái và chỉ được chứa chữ cái, số và khoảng trắng!"
    )
    private String name;

    @NotNull(message = "Giá không được để trống!")
    @DecimalMin(value = "0", inclusive = false, message = "Giá phải lớn hơn 0!")
    private Double price;

    private String image;

    @NotNull
    @NotEmpty(message = "Miêu tả chi tiết không được để trống!")
    @Column(columnDefinition = "TEXT")
    private String detailDesc;

    @NotNull
    @NotEmpty(message = "Miêu tả ngắn gọn không được để trống!")
    private String shortDesc;

    @NotNull(message = "Số lượng không được để trống!")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1!")
    private Long quantity;

    private long sold;

    private String factory;

    private String target;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public long getSold() {
        return sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
