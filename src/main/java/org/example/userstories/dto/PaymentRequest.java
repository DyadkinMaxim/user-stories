package org.example.userstories.dto;

public record PaymentRequest(
        Double amount,
        String currency,
        String accountId,
        String toIban
) {}
