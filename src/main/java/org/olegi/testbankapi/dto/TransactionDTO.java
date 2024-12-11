package org.olegi.testbankapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.olegi.testbankapi.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionTypes transactionType;
}
