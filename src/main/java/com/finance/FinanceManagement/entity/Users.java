package com.finance.FinanceManagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String fullName;

    private String email;

    private String passwordHash;

    private String role;

    private Boolean isFirstTimeLogin;

    private String twoFactorSecret; //access
    private String refreshToken;
}
