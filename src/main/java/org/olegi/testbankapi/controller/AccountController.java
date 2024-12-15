package org.olegi.testbankapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Создание нового аккаунта", description = "Позволяет создать новый аккаунт с помощью предоставленных данных.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Аккаунт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        String accountId = accountService.createAccount(accountDTO);
        URI location = URI.create("/accounts/" + accountId);
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Обновление данных аккаунта", description = "Позволяет обновить информацию об аккаунте по идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @PatchMapping("/{accountNumber}")
    public ResponseEntity<Void> updateAccount(@PathVariable String accountNumber,
                                              @Valid @RequestBody AccountDTO accountDTO) {
        accountService.updateAccount(accountNumber, accountDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение информации об аккаунте", description = "Возвращает данные об аккаунте по идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация об аккаунте получена"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccountInfo(@Valid @PathVariable String accountNumber) {
        AccountDTO accountDTO = accountService.getAccountInfo(accountNumber);
        return ResponseEntity.ok(accountDTO);
    }

    @Operation(summary = "Удаление аккаунта", description = "Удаляет аккаунт по идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Аккаунт успешно удален"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@Valid @PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
