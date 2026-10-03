package com.finance.FinanceManagement.service;

import com.finance.FinanceManagement.dto.*;
import com.finance.FinanceManagement.entity.Users;
import com.finance.FinanceManagement.repository.UserRepository;
import com.google.zxing.WriterException;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    private final JavaMailSender mailSender;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorService twoFactorService;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, JwtService jwtService, TwoFactorService twoFactorService,JavaMailSender mailSender) {

        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.twoFactorService = twoFactorService;
        this.mailSender = mailSender;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        Users users = modelMapper.map(userRequest, Users.class);
        users.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));
        users.setIsFirstTimeLogin(true);
        Users savedUser = userRepository.save(users);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Users users = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), users.getPasswordHash())) {
            throw new RuntimeException("Wrong password");
        }
        LoginResponse response = new LoginResponse();
        response.setId(users.getUserId());
        response.setFullName(users.getFullName());
        response.setEmail(users.getEmail());
        response.setRole(users.getRole());
        response.setIsFirstTimeLogin(users.getIsFirstTimeLogin());

  //     if (users.getIsFirstTimeLogin())
//               && "USER".equalsIgnoreCase(users.getRole()))

           if (users.getIsFirstTimeLogin()
                   && users.getTwoFactorSecret() == null){
               String secret = twoFactorService.generateSecret();
            users.setTwoFactorSecret(secret);
            userRepository.save(users);
            String qrUrl = twoFactorService.generateQrCodeUrl(secret, users.getEmail());
            try {
                String qrCode = twoFactorService.generateQrCode(qrUrl);
                response.setQrCode(qrCode);
            } catch (Exception e) {
                throw new RuntimeException("QR code generation failed", e);
            }

        }
        UserDetails userDetails = User
                .withUsername(users.getEmail()) .password(users.getPasswordHash()) .roles(users.getRole()) .build();
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        users.setRefreshToken(refreshToken);
        userRepository.save(users);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }


    private final Map<String, String> forgotPasswordOtps = new HashMap<>();
    private final Map<String, Boolean> passwordResetVerified = new HashMap<>();

    public void updatePassword(String email, String newPassword) {
        Users users = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not registered"));
        users.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(users);
    }
    @Override
    public void sendForgotPasswordOtp(String email) {
        userRepository.findByEmail(email).orElseThrow(() ->
                        new RuntimeException("Email not registered"));
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        forgotPasswordOtps.put(email, otp);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Finance Management - Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\n\n" + "OTP valid for password reset.");
        mailSender.send(message);
//        System.out.println("AFTER MAIL");
    }

    @Override
    public boolean verifyForgotPasswordOtp(String email, String otp) {
        String savedOtp = forgotPasswordOtps.get(email);
        if (savedOtp == null) {return false;}
        if (savedOtp.equals(otp)) {
            forgotPasswordOtps.remove(email);
            return true;
        }
        return false;
    }

    @Override
    public void markPasswordResetVerified(String email) {
        passwordResetVerified.put(email, true);}

    @Override
    public boolean isPasswordResetVerified(String email) {
        return passwordResetVerified.getOrDefault(email, false);
    }
    }
