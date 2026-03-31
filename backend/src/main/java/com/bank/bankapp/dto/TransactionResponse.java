package com.bank.bankapp.dto;

import com.bank.bankapp.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Long fromAccountId;
    private Long toAccountId;
    private boolean incoming;
}
