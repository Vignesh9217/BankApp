package com.bank.bankapp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDetailsResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String fullName;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
