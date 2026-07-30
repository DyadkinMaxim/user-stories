package org.example.userstories.payment;

public record PaymentRequest(
        Double amount,
        String currency,
        String accountId,
        String toIban
) {}
