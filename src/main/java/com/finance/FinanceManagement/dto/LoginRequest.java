package com.finance.FinanceManagement.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data

public class LoginRequest {
    @NotBlank(message = "email needed")@Email(message = "invalid email")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;


}
