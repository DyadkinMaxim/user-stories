package org.example.userstories.dto;

import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
        Double amount,
        String currency,
        @Pattern(
                regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]+$",
                message = "IBAN must start with 2 letters followed by 2 digits followed by alphanumeric."
        )
        String toIban
) {}
