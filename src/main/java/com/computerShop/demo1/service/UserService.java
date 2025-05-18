package com.computerShop.demo1.service;

import com.computerShop.demo1.domain.*;
import com.computerShop.demo1.domain.dto.RegisterDTO;
import com.computerShop.demo1.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
        private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       CartRepository cartRepository,
                       CartDetailRepository cartDetailRepository,
                       OrderRepository orderRepository,
                       OrderDetailRepository orderDetailRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public Optional<User> getUserById(long id) {
        return this.userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean checkUserEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public long countAllUsers() {
        return this.userRepository.count();
    }

    public List<Role> getAllRoles() {
        return this.roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    @Transactional
    public void deleteUserById(long id, HttpSession session) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            User userGetById = userOptional.get();

            List<Order> orders = this.orderRepository.findByUser(userGetById);
            if (!orders.isEmpty()) {
                throw new IllegalStateException("Deletion failed: User had active orders!");
            }

            Cart cartGetByUser = this.cartRepository.findByUser(userGetById);
            if (cartGetByUser != null) {
                List<CartDetail> cartDetails = cartGetByUser.getCartDetails();
                this.cartDetailRepository.deleteAll(cartDetails);
                this.cartRepository.delete(cartGetByUser);
            }

            this.userRepository.delete(userGetById);
            session.setAttribute("sum", 0);
        }
    }

    public User handleRegisterDTOtoUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;
    }
}
