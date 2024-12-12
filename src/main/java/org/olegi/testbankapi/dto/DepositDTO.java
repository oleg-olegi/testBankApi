package org.olegi.testbankapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(description = "DTO representing a deposit request")
public class DepositDTO {

    @Schema(description = "Account number where the deposit will be made",
            example = "1234567890AB")
    @NotNull(message = "Account number must not be null")
    @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must contain only letters and digits")
    private String accountNumber;

    @Schema(description = "Amount to deposit",
            example = "1000.00")
    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits before the decimal point")
    private BigDecimal amount;
}