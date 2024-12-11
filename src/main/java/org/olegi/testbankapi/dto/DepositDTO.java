package org.olegi.testbankapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DepositDTO {
    private String accountNumber;
    private BigDecimal amount;
}