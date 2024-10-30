package io.assesment.currencyexchangeanddiscountcalculation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillCalculationRequest {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "^(employee|affiliate|customer)$", message = "Invalid user type")
    private String userType;

    @Min(value = 0, message = "Tenure must be non-negative")
    private int tenure;

    @NotBlank(message = "Original currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Invalid currency format")
    private String originalCurrency;

    @NotBlank(message = "Target currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Invalid currency format")
    private String targetCurrency;

    private boolean isGrocery;
}

