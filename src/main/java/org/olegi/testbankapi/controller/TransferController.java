package org.olegi.testbankapi.controller;

import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.TransferRequestDTO;
import org.olegi.testbankapi.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Validated
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/transfer")
    public ResponseEntity<?> doTransfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        transferService.transferMoney(
                transferRequestDTO.getFromAccountNumber(),
                transferRequestDTO.getToAccountNumber(),
                transferRequestDTO.getAmount());
        return ResponseEntity.ok().build();
    }
}