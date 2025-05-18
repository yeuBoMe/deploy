package com.computerShop.demo1.controller.admin;

import com.computerShop.demo1.domain.Role;
import com.computerShop.demo1.domain.User;
import com.computerShop.demo1.service.UploadService;
import com.computerShop.demo1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;

    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          UploadService uploadService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/user")
    public String getListUsersPage(Model model,
                                  @RequestParam(value = "page", required = false) Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {

            }
        } catch (Exception e) {

        }

        Pageable pageable = PageRequest.of(page - 1, 3);
        Page<User> pageHavUsers = this.userService.getAllUsers(pageable);
        List<User> users = pageHavUsers.getContent();
        int allPages = pageHavUsers.getTotalPages();

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("allPages", allPages);

        return "admin/user/list-users";
    }

    @GetMapping("/admin/user/{id}")
    public String getDetailUserPage(Model model, @PathVariable long id) {
        User userGetById = this.userService.getUserById(id).get();
        if (userGetById != null) {
            model.addAttribute("user", userGetById);
            model.addAttribute("id", id);
        }
        return "admin/user/detail-user";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        List<Role> roles = this.userService.getAllRoles();
        model.addAttribute("createUser", new User());
        model.addAttribute("roles", roles);
        return "admin/user/create-user";
    }

    @PostMapping("/admin/user/create")
    public String handleCreateUserRequest(@ModelAttribute("createUser") @Valid User user,
                                          BindingResult createUserBindingResult,
                                          @RequestParam("userAvatar") MultipartFile multipartFile,
                                          Model model) {
        List<FieldError> errors = createUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>>>" + error.getField() + "-" + error.getDefaultMessage());
        }

        // check if email exists
        if (this.userService.checkUserEmailExists(user.getEmail())) {
            createUserBindingResult.addError(
                    new FieldError(
                            "createUser",
                            "email",
                            "Email " + user.getEmail() + " đã tồn tại!"
                    )
            );
        }

        if (createUserBindingResult.hasErrors()) {
            List<Role> roles = this.userService.getAllRoles();
            model.addAttribute("createUser", user);
            model.addAttribute("roles", roles);
            return "admin/user/create-user";
        }

        String avatar = this.uploadService.handleUploadUserAvatar(multipartFile);
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        Role role = this.userService.getRoleByName(user.getRole().getName());

        user.setAvatar(avatar);
        user.setPassword(hashPassword);
        user.setRole(role);

        this.userService.handleSaveUser(user);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User userGetById = this.userService.getUserById(id).get();
        if (userGetById != null) {
            List<Role> roles = this.userService.getAllRoles();
            model.addAttribute("updateUser", userGetById);
            model.addAttribute("roles", roles);
            model.addAttribute("id", id);
        }
        return "admin/user/update-user";
    }

    @PostMapping("/admin/user/update")
    public String handleUpdateUserRequest(@ModelAttribute("updateUser") @Valid User user,
                                          BindingResult updateUserBindingResult,
                                          @RequestParam("userAvatar") MultipartFile multipartFile,
                                          Model model) {
        List<FieldError> errors = updateUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>>>>" + error.getField() + "-" + error.getDefaultMessage());
        }

        User currentUser = this.userService.getUserById(user.getId()).get();
        String avatar = this.uploadService.handleUploadUserAvatar(multipartFile);
        Role roleChange = this.userService.getRoleByName(user.getRole().getName());

        if (updateUserBindingResult.hasErrors()) {
            List<Role> roles = this.userService.getAllRoles();
            model.addAttribute("updateUser", user);
            model.addAttribute("roles", roles);
            user.setAvatar(currentUser.getAvatar());
            return "admin/user/update-user";
        }

        if (currentUser != null) {
            currentUser.setFullName(user.getFullName());
            currentUser.setAddress(user.getAddress());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setRole(roleChange);
            if (!multipartFile.isEmpty()) {
                currentUser.setAvatar(avatar);
            }
            this.userService.handleSaveUser(currentUser);
        }

        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        User userGetById = this.userService.getUserById(id).get();
        if (userGetById != null) {
            model.addAttribute("deleteUser", userGetById);
            model.addAttribute("id", id);
        }
        return "admin/user/delete-user";
    }

    @PostMapping("/admin/user/delete")
    public String handleDeleteUserRequest(@ModelAttribute("deleteUser") User user,
                                          HttpServletRequest request,
                                          Model model) {
        HttpSession session = request.getSession(false);
        try {
            this.userService.deleteUserById(user.getId(), session);
            return "redirect:/admin/user";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("deleteUser", user);
            model.addAttribute("id", user.getId());
            return "admin/user/delete-user";
        }
    }
}


