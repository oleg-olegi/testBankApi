package org.olegi.testbankapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.olegi.testbankapi.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "DTO representing a transaction")
public class TransactionDTO {

    @Schema(description = "Transaction amount",
            example = "1234.56")
    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits before the decimal point")
    private BigDecimal amount;

    @Schema(description = "Timestamp of the transaction",
            example = "2024-12-11T10:15:30")
    @NotNull(message = "Timestamp must not be null")
    private LocalDateTime timestamp;

    @Schema(description = "Type of the transaction",
            example = "DEPOSIT")
    @NotNull(message = "Transaction type must not be null")
    private TransactionTypes transactionType;
}
