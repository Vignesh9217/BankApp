package com.bank.bankapp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String toAccountNumber;
    private BigDecimal amount;
}