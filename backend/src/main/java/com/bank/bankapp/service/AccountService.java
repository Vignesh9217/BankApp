package com.bank.bankapp.service;

import com.bank.bankapp.dto.*;
import com.bank.bankapp.entity.Account;
import com.bank.bankapp.entity.Transaction;
import com.bank.bankapp.entity.User;
import com.bank.bankapp.repository.AccountRepository;
import com.bank.bankapp.repository.TransactionRepository;
import com.bank.bankapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AccountResponse getAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setFullName(user.getFullName());
        return response;
    }

    @Transactional
    public AccountResponse deposit(String username, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        account = accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setType(Transaction.TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setToAccount(account);
        transactionRepository.save(tx);

        return getAccount(username);
    }

    @Transactional
    public AccountResponse transfer(String username, TransferRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        User fromUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account fromAccount = accountRepository.findByUser(fromUser)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new RuntimeException("Cannot transfer to your own account");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction tx = new Transaction();
        tx.setType(Transaction.TransactionType.TRANSFER);
        tx.setAmount(request.getAmount());
        tx.setFromAccount(fromAccount);
        tx.setToAccount(toAccount);
        transactionRepository.save(tx);

        return getAccount(username);
    }

    public List<TransactionResponse> getTransactions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        return transactionRepository.findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
                account.getId(), account.getId()).stream()
                .map(transaction -> mapTransaction(transaction, account.getId()))
                .toList();
    }

    public List<AccountDetailsResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapAccountDetails)
                .toList();
    }

    private TransactionResponse mapTransaction(Transaction transaction, Long currentAccountId) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setIncoming(isIncomingTransaction(transaction, currentAccountId));
        response.setFromAccountId(transaction.getFromAccount() != null ? transaction.getFromAccount().getId() : null);
        response.setToAccountId(transaction.getToAccount() != null ? transaction.getToAccount().getId() : null);
        return response;
    }

    private boolean isIncomingTransaction(Transaction transaction, Long currentAccountId) {
        if (transaction.getType() == Transaction.TransactionType.DEPOSIT) {
            return true;
        }

        return transaction.getToAccount() != null
                && currentAccountId.equals(transaction.getToAccount().getId());
    }

    private AccountDetailsResponse mapAccountDetails(Account account) {
        AccountDetailsResponse response = new AccountDetailsResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setBalance(account.getBalance());
        response.setCreatedAt(account.getCreatedAt());
        response.setFullName(account.getUser().getFullName());
        response.setUsername(account.getUser().getUsername());
        response.setEmail(account.getUser().getEmail());
        return response;
    }
}
