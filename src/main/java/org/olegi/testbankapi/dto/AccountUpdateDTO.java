package org.olegi.testbankapi.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

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
}