package org.olegi.testbankapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing a transfer request")
public class TransferRequestDTO {

    @Schema(description = "Account number of the sender",
            example = "1234567890AB")
    @NotNull(message = "From-account number must not be null")
    @Size(min = 10, max = 20, message = "From-account number must be between 10 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "From-account number must contain only letters and digits")
    private String fromAccountNumber;

    @Schema(description = "Account number of the receiver",
            example = "9876543210XY")
    @NotNull(message = "To-account number must not be null")
    @Size(min = 10, max = 20, message = "To-account number must be between 10 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "To-account number must contain only letters and digits")
    private String toAccountNumber;

    @Schema(description = "Transfer amount",
            example = "2500.50")
    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits before the decimal point")
    private BigDecimal amount;
}
