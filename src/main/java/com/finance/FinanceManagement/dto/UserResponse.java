package com.finance.FinanceManagement.dto;

import lombok.Data;

@Data
public class UserResponse {

    private Integer id;
    private String fullName;
    private String email;
    private String role;
    private Boolean isFirstTimeLogin;


}