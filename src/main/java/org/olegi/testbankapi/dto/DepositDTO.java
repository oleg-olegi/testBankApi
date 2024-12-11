package org.olegi.testbankapi.dto;

import lombok.Data;
import org.olegi.testbankapi.enums.TransactionTypes;

import java.math.BigDecimal;

@Data
public class DepositDTO {
    private String accountNumber;
    private BigDecimal amount;
}