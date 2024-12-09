package org.olegi.testbankapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawDTO {
    private String accountNumber;
    private BigDecimal amount;
}