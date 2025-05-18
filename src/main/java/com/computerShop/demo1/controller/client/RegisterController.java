package com.computerShop.demo1.controller.client;

import com.computerShop.demo1.domain.Role;
import com.computerShop.demo1.domain.User;
import com.computerShop.demo1.domain.dto.RegisterDTO;
import com.computerShop.demo1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RegisterController {
private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String handleRegisterRequest(@ModelAttribute("registerUser") @Valid RegisterDTO registerDTO,
                                        BindingResult registerUserBindingResult,
                                        Model model) {
        List<FieldError> errors = registerUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>>" + error.getField() + "-" + error.getDefaultMessage());
        }

        if (registerUserBindingResult.hasErrors()) {
            model.addAttribute("registerUser", registerDTO);
            return "client/auth/register";
        }

        User userAddByRegisterDTO = this.userService.handleRegisterDTOtoUser(registerDTO);
        String hashPassword = this.passwordEncoder.encode(userAddByRegisterDTO.getPassword());
        Role role = this.userService.getRoleByName("USER");

        userAddByRegisterDTO.setPassword(hashPassword);
        userAddByRegisterDTO.setRole(role);

        this.userService.handleSaveUser(userAddByRegisterDTO);
        return "redirect:/login";
    }
}
