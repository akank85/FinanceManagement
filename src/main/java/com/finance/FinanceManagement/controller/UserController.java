package com.finance.FinanceManagement.controller;

import com.finance.FinanceManagement.dto.UserRequest;
import com.finance.FinanceManagement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Controller
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {this.userService = userService;}
    @PostMapping("/users")
    public String createUser(UserRequest userRequest) {
        userService.createUser(userRequest);
        return "redirect:/login";
    }

}
