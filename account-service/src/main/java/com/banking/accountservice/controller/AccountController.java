package com.banking.accountservice.controller;

import com.banking.accountservice.dto.AccountResponse;
import com.banking.accountservice.dto.CreateAccountRequest;
import com.banking.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid  @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<String> blockAccount(@PathVariable String accountNumber) {
        accountService.blockAccount(accountNumber);
        return ResponseEntity.ok(("Account Blocked Successfully"));
    }

    /*
     * SAGA STEP 1: Deduct Balance
     * Called by Transaction Service when transfer is initiated
     */
    @PutMapping("/{accountNumber}/deduct")
    public ResponseEntity<String> deductBalance(@PathVariable String accountNumber, @RequestBody BigDecimal deductAmount) {
        accountService.deductBalance(accountNumber,deductAmount);
        return ResponseEntity.ok("Balance deducted Successfully");
    }

    /*
     * SAGA STEP 4: Compensating transaction endpoint
     * Called by Transaction Service in two scenarios:
     * 1. Fraud Detected -> refund sender(under step 1)
     * 2. Transaction completed -> credit receiver
     *
     */
    @PutMapping("/{accountNumber}/credit")
    public ResponseEntity<String> creditBalance(@PathVariable String accountNumber, @RequestBody BigDecimal creditAmount) {
        accountService.creditBalance(accountNumber,creditAmount);
        return ResponseEntity.ok("Balance credited Successfully");
    }
}
