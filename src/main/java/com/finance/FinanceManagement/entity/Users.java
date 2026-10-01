package com.finance.FinanceManagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Integer userId;

    @Column(name = "FullName", nullable = false)
    private String fullName;

    @Column(name = "Email", nullable = false)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "Role", nullable = false)
    private String role;

    @Column(name = "IsFirstTimeLogin", nullable = false)
    private Boolean isFirstTimeLogin;

    @Column(name = "SecretKey")
    private String secretKey;
}

