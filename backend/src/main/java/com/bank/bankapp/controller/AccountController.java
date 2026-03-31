package com.bank.bankapp.controller;

import com.bank.bankapp.dto.*;
import com.bank.bankapp.entity.User;
import com.bank.bankapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<AccountResponse> getAccount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccount(user.getUsername()));
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @AuthenticationPrincipal User user,
            @RequestBody DepositRequest request) {
        return ResponseEntity.ok(accountService.deposit(user.getUsername(), request.getAmount()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<AccountResponse> transfer(
            @AuthenticationPrincipal User user,
            @RequestBody TransferRequest request) {
        return ResponseEntity.ok(accountService.transfer(user.getUsername(), request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getTransactions(user.getUsername()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AccountDetailsResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
}
