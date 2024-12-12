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
@Schema(description = "DTO representing an account updating request")
public class AccountUpdateDTO {
    @Schema(description = "Account number where the updating will be made",
            example = "1234567890AB")
    @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must contain only letters and digits")
    private String accountNumber;

    @Schema(description = "Balance of account",
            example = "1000.00")
    @PositiveOrZero(message = "Balance must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "Balance must have up to 10 digits before the decimal point and up to 2 digits after it")
    private BigDecimal balance;
}