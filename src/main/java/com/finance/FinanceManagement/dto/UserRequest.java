package com.finance.FinanceManagement.dto;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data

public class UserRequest {

    private String fullName;
    private String email;
    private String passwordHash;
    private String role;
}
