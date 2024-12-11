package org.olegi.testbankapi.controller;

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

    @PostMapping("/createAccount")
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        String accountId = accountService.createAccount(accountDTO);
        URI location = URI.create("/accounts/" + accountId);
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/updateAccount/{accountId}")
    public ResponseEntity<Void> updateAccount(@PathVariable String accountId,
                                              @Valid @RequestBody AccountUpdateDTO accountUpdateDTO) {
        accountService.updateAccount(accountId, accountUpdateDTO);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/getAccountInfo/{accountId}")
    public ResponseEntity<AccountDTO> getAccountInfo(@Valid @PathVariable String accountId) {
        AccountDTO accountDTO = accountService.getAccountInfo(accountId);
        return ResponseEntity.ok(accountDTO);
    }

    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<Void> deleteAccount(@Valid @PathVariable String accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
