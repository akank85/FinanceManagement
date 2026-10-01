package com.finance.FinanceManagement.dto;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;

@Data


public class LoginRequest {
    private String email;
    private String password;


}
