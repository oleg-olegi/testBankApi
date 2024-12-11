package org.olegi.testbankapi.controller;

import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.TransferRequestDTO;
import org.olegi.testbankapi.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);
    private final TransferService transferService;

    @PostMapping("/transfer")
    public ResponseEntity<?> doTransfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        log.info("Transfer request: {}", transferRequestDTO.toString());
        transferService.transferMoney(transferRequestDTO);
        return ResponseEntity.ok("Transfer successful");
    }
}