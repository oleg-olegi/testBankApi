package org.olegi.testbankapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.AccountUpdateDTO;
import org.olegi.testbankapi.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Создание нового аккаунта", description = "Позволяет создать новый аккаунт с помощью предоставленных данных.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Аккаунт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping("/createAccount")
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
    @PutMapping("/updateAccount/{accountId}")
    public ResponseEntity<Void> updateAccount(@PathVariable String accountId,
                                              @Valid @RequestBody AccountUpdateDTO accountUpdateDTO) {
        accountService.updateAccount(accountId, accountUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение информации об аккаунте", description = "Возвращает данные об аккаунте по идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация об аккаунте получена"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @GetMapping("/getAccountInfo/{accountId}")
    public ResponseEntity<AccountDTO> getAccountInfo(@Valid @PathVariable String accountId) {
        AccountDTO accountDTO = accountService.getAccountInfo(accountId);
        return ResponseEntity.ok(accountDTO);
    }

    @Operation(summary = "Удаление аккаунта", description = "Удаляет аккаунт по идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Аккаунт успешно удален"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<Void> deleteAccount(@Valid @PathVariable String accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
