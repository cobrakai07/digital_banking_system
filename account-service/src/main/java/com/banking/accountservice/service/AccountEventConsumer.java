package com.banking.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountEventConsumer {

    private final AccountService accountService;

    public void consumeTransactionCompleted(@Payload Map<String, Object> payload) {
        try{
            String receiverAccount = (String) payload.get("receiverAccountNumber");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());

            log.info("Crediting account {} amount {}", receiverAccount, amount);
            accountService.creditBalance(receiverAccount,amount);
        }catch (Exception e){
            log.error("Error while crediting account.\n {}" ,e.getMessage());
        }
    }

    public void consumerFraudDetected(@Payload Map<String, Object> payload) {
        try{
            String accountNumber = (String) payload.get("accountNumber");
            log.info("Fraud Detected. Blocking account {}", accountNumber);
            accountService.blockAccount(accountNumber);
        }catch (Exception e){
            log.error("Error while blocking account {}" ,e.getMessage());
        }
    }
}
