package com.finance.FinanceManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/2fa")
    public String twoFactorPage() {
        return "2fa";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/forgot-password-otp")
    public String forgotPasswordOtpPage() {
        return "forgot-password-otp";
    }

    @GetMapping("/forgot-password-authenticator")
    public String forgotPasswordAuthenticatorPage() {
        return "forgot-password-authenticator";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }
}