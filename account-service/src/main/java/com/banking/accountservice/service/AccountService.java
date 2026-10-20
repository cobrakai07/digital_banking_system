package com.banking.accountservice.service;

import com.banking.accountservice.dto.AccountResponse;
import com.banking.accountservice.dto.CreateAccountRequest;
import com.banking.accountservice.entity.Account;
import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;
import com.banking.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private static SecureRandom secureRandom = new SecureRandom();

    public AccountResponse getAccount(String accountNumber) {
        Account account =  accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        return mapToResponse(account);
    }

    public void creditBalance(String accountNumber, BigDecimal creditAmount) {
        log.info("Credit balance for account {} is {}", accountNumber, creditAmount);
        Account account =  accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        account.setBalance(account.getBalance().add(creditAmount));
        accountRepository.save(account);
        log.info("Credited for account {} New balance {}", accountNumber, account.getBalance());
    }

    public void deductBalance(String accountNumber, BigDecimal deductAmount) {
        log.info("Deducting balance for account {}", accountNumber);
        Account account =  accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        if(account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account not active "+accountNumber);
        }

        if(account.getBalance().compareTo(deductAmount) < 0) {
            throw new RuntimeException("Insufficient balance for account "+accountNumber);
        }

        account.setBalance(account.getBalance().subtract(deductAmount));
        accountRepository.save(account);

        log.info("Balance updated for account {} New balance {}", account.getAccountNumber(),  account.getBalance());
    }

    public void blockAccount(String accountNumber) {
        log.info("Blocking account {}", accountNumber);
        Account account =  accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
        log.info("Account {} blocked", account.getAccountNumber());
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account =  accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        return account.getBalance();
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for : {}", request.getEmail());
        if(accountRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("account already exists for email : " + request.getEmail());
        }

        Account account = Account.builder()
                .accountHolderName(request.getAccountHolderName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .accountType(request.getAccountType())
                .status(AccountStatus.ACTIVE)
                .balance(request.getInitialDeposit())
                .accountNumber(generateAccountNumber())
                .dailyTransactionalLimit(request.getAccountType() == AccountType.SAVINGS
                ? new BigDecimal(100000)
                : new BigDecimal(500000))
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Saved account : {}", savedAccount.getAccountNumber());

        return mapToResponse(savedAccount);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do{
            long number = secureRandom.nextLong(1_000_000_000_000L);
            accountNumber = String.format("%012d", number);
        }while(accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    public AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .dailyTransactionalLimit(account.getDailyTransactionalLimit())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
