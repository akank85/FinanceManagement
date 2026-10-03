package com.finance.FinanceManagement.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private Integer id;
    private String fullName;
    private String email;
    private String role;
    private Boolean isFirstTimeLogin;
    private String qrCode;

    private String refreshToken;
}