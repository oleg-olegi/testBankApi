package org.olegi.testbankapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.DepositDTO;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.dto.WithdrawDTO;
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
    @Operation(summary = "Пополнение счета", description = "Позволяет пополнить счет на указанную сумму.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Счет успешно пополнен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping("/deposit")
    public ResponseEntity<AccountDTO> deposit(@RequestBody DepositDTO depositDTO) {
        AccountDTO accountDTO = transactionService.deposit(depositDTO);
        return ResponseEntity.ok(accountDTO);
    }
    @Operation(summary = "Снятие со счета", description = "Позволяет снять деньги со счета.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Снятие выполнено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "403", description = "Недостаточно средств")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<AccountDTO> withdraw(@RequestBody WithdrawDTO withdrawDTO) {
        AccountDTO accountDTO = transactionService.withdraw(withdrawDTO);
        return ResponseEntity.ok(accountDTO);
    }
    @Operation(summary = "Получение баланса", description = "Возвращает текущий баланс счета.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
            @ApiResponse(responseCode = "404", description = "Счет не найден")
    })
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestParam String accountNumber) {
        BigDecimal balance = transactionService.getBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }
    @Operation(summary = "История операций", description = "Возвращает список операций за указанный период.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "История операций успешно получена"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getOperationsHistory(
            @RequestParam("accountId") Long accountId,
            @RequestParam("from") LocalDateTime from,
            @RequestParam("to") LocalDateTime to) {

        List<TransactionDTO> statement = transactionService.getOperationHistory(accountId, from, to);
        return ResponseEntity.ok(statement);
    }
}
