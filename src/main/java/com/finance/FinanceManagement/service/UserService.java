package com.finance.FinanceManagement.service;

import com.finance.FinanceManagement.dto.LoginRequest;
import com.finance.FinanceManagement.dto.LoginResponse;
import com.finance.FinanceManagement.dto.UserRequest;
import com.finance.FinanceManagement.entity.Users;
import com.finance.FinanceManagement.dto.UserResponse;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);

    LoginResponse login(LoginRequest loginRequest);

    void updatePassword(String email, String newPassword);
    void sendForgotPasswordOtp(String email);
    boolean verifyForgotPasswordOtp(String email, String otp);
    void markPasswordResetVerified(String email);

    boolean isPasswordResetVerified(String email);
}