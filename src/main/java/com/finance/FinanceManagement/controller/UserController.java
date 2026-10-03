package com.finance.FinanceManagement.controller;
import com.finance.FinanceManagement.dto.*;
import com.finance.FinanceManagement.entity.Users;
import com.finance.FinanceManagement.repository.UserRepository;
import com.finance.FinanceManagement.service.JwtService;
import com.finance.FinanceManagement.service.TwoFactorService;
import com.finance.FinanceManagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TwoFactorService twoFactorService;

    public UserController(
            UserService userService,
            JwtService jwtService,
            UserRepository userRepository,
            TwoFactorService twoFactorService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.twoFactorService = twoFactorService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(
            @RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/2fa/qr")
    public ResponseEntity<String> generateQr(
            @RequestParam String email) {
        Users users = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));
        String qrUrl = twoFactorService.generateQrCodeUrl(
                users.getTwoFactorSecret(),
                users.getEmail()
        );
        return ResponseEntity.ok(qrUrl);
    }



    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        Users users = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        boolean valid = twoFactorService.verifyOtp(
                users.getTwoFactorSecret(),
                Integer.parseInt(request.getOtp()));
        if (!valid) {return ResponseEntity.badRequest()
                .body("Invalid OTP");
        }
        users.setIsFirstTimeLogin(false);
        userRepository.save(users);

        UserDetails userDetails = User.withUsername(users.getEmail())
                .password(users.getPasswordHash())
                .roles(users.getRole())
                .build();

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        users.setRefreshToken(refreshToken);
        userRepository.save(users);
        LoginResponse response = new LoginResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setId(users.getUserId());
        response.setFullName(users.getFullName());
        response.setEmail(users.getEmail());
        response.setRole(users.getRole());
        response.setIsFirstTimeLogin(false);
        return ResponseEntity.ok(response);


    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        try {userService.sendForgotPasswordOtp(request.getEmail());
            System.out.println("OTP SENT SUCCESSFULLY");
            return ResponseEntity.ok("OTP sent successfully to registered email");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @PostMapping("/verify-forgot-password-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(
            @RequestBody ForgotPasswordOtpRequest request) {
        boolean valid = userService.verifyForgotPasswordOtp(
                        request.getEmail(),
                        request.getOtp());
        if (!valid) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired OTP");
        }
        return ResponseEntity.ok(
                "Email OTP verified"
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        try {
            if (!userService.isPasswordResetVerified(
                    request.getEmail())) {
                return ResponseEntity.status(403)
                        .body("OTP verification required");
            }
            userService.updatePassword(
                    request.getEmail(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(
                    "Password reset successfully"
            );
        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @PostMapping("/verify-forgot-password-authenticator")
    public ResponseEntity<?> verifyForgotPasswordAuthenticator(
            @RequestBody ForgotPasswordOtpRequest request) {
        Users users = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        boolean valid = twoFactorService.verifyOtp(
                users.getTwoFactorSecret(),
                Integer.parseInt(request.getOtp())
        );
        if (!valid) {
            return ResponseEntity.badRequest()
                    .body("Invalid Authenticator OTP");
        }
        userService.markPasswordResetVerified(
                request.getEmail()
        );
        return ResponseEntity.ok(
                "Authenticator OTP verified"
        );
    }
}




