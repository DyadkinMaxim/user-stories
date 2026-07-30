package org.example.userstories.model;

public record PaymentRequest(
        Double amount,
        String currency,
        String accountId,
        String toIban
) {}
