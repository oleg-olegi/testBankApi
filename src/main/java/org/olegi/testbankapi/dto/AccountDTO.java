package org.olegi.testbankapi.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private String accountNumber;
    private BigDecimal balance;
}
