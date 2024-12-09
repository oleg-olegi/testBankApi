package org.olegi.testbankapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}