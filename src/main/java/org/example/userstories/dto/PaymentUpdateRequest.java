package org.example.userstories.dto;

public record PaymentUpdateRequest (
    Double amount,
    String currency,
    String accountId,
    String toIban
) {}
