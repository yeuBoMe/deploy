package com.computerShop.demo1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

/* table 'users' chứa role_id => User là owner side*/
@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(unique = true)
    @Email(message = "Email không hợp lệ")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email sai định dạng!")
    private String email;

    @Size(min = 5, message = "Mật khẩu tối thiểu 5 kí tự!")
    private String password;

    @NotNull
    @Size(min = 5, message = "Họ tên tối thiểu 5 kí tự!")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ tên chỉ chứa chữ và khoảng trắng!")
    private String fullName;

    @Size(min = 5, message = "Địa chỉ tối thiểu 5 kí tự!")
    @Pattern(regexp = "^[\\p{L}0-9\\s,.-]+$", message = "Địa chỉ chỉ chứa chữ cái, số và một số ký tự(, . -)")
    private String address;

    @Pattern(regexp = "^0\\d{9,}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 kí tự!")
    private String phoneNumber;

    private String avatar;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
