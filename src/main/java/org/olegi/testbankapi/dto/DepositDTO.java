package org.olegi.testbankapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositDTO {
    private String accountNumber;
    private BigDecimal amount;
}