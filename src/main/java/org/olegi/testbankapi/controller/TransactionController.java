package org.olegi.testbankapi.controller;

import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.*;
import org.olegi.testbankapi.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@AllArgsConstructor
@Validated

public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<AccountDTO> deposit(@RequestBody DepositDTO depositDTO) {
        AccountDTO accountDTO = transactionService.deposit(depositDTO);
        return ResponseEntity.ok(accountDTO);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountDTO> withdraw(@RequestBody WithdrawDTO withdrawDTO) {
        AccountDTO accountDTO = transactionService.withdraw(withdrawDTO);
        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestParam String accountNumber) {
        BigDecimal balance = transactionService.getBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getOperationsHistory(
            @RequestParam("accountId") Long accountNumber,
            @RequestParam("from") LocalDateTime from,
            @RequestParam("to") LocalDateTime to) {

        List<TransactionDTO> statement = transactionService.getOperationHistory(accountNumber, from, to);
        return ResponseEntity.ok(statement);
    }
}
