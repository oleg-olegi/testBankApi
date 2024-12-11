package org.olegi.testbankapi.dto;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @NotNull(message = "Account number must not be null")
    @Size(min = 10, max = 20, message = "Account number must be between 10 and 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must contain only letters and digits")
    private String accountNumber;

    @NotNull(message = "Balance must not be null")
    @PositiveOrZero(message = "Balance must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "Balance must have up to 10 digits before the decimal point and up to 2 digits after it")
    private BigDecimal balance;
}
