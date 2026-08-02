package org.example.userstories.model;

public record PaymentUpdateRequest (
    Double amount,
    String currency,
    String accountId,
    String toIban
) {}
