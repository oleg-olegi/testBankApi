package org.olegi.testbankapi.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.olegi.testbankapi.dto.TransferRequestDTO;
import org.olegi.testbankapi.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/transfer")
    public ResponseEntity<?> doTransfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        log.info("Received transfer request from account {} to account {} for amount {}",
                transferRequestDTO.getFromAccountNumber(),
                transferRequestDTO.getToAccountNumber(),
                transferRequestDTO.getAmount());
        try {
            transferService.transferMoney(
                    transferRequestDTO.getFromAccountNumber(),
                    transferRequestDTO.getToAccountNumber(),
                    transferRequestDTO.getAmount());

            log.info("Successfully transferred amount {} from account {} to account {}",
                    transferRequestDTO.getAmount(), transferRequestDTO.getFromAccountNumber(),
                    transferRequestDTO.getToAccountNumber());

            return ResponseEntity.ok().build();
        } catch (Exception e) {

            log.error("Error during transfer from account {} to account {} for amount {}",
                    transferRequestDTO.getFromAccountNumber(),
                    transferRequestDTO.getToAccountNumber(),
                    transferRequestDTO.getAmount(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}