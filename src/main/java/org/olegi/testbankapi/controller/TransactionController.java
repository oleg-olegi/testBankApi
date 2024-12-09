package org.olegi.testbankapi.controller;

import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.DepositDTO;
import org.olegi.testbankapi.dto.OperationHistoryDTO;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.dto.WithdrawDTO;
import org.olegi.testbankapi.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestBody DepositDTO depositRequest) {
        transactionService.deposit(depositRequest.getAccountNumber(), depositRequest.getAmount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody WithdrawDTO withdrawRequest) {
        transactionService.withdraw(withdrawRequest.getAccountNumber(), withdrawRequest.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestParam String accountNumber) {
        BigDecimal balance = transactionService.getBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getOperationsHistory(@RequestBody OperationHistoryDTO request) {
        List<TransactionDTO> statement = transactionService.getStatement(
                request.getAccountNumber(), request.getFrom(), request.getTo());
        return ResponseEntity.ok(statement);
    }
}
