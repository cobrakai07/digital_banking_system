package com.banking.accountservice.dto;


import com.banking.accountservice.entity.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {
    @NotBlank(message = "Account Holder name is required")
    private String accountHolderName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid format email")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Initial deposit is required")
    @Positive(message = "Initial deposit must be positive")
    private BigDecimal initialDeposit;

    @NotBlank(message = "Account Type is required")
    private AccountType accountType;
}
