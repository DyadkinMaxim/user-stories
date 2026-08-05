package org.example.userstories.dto;

import org.example.userstories.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        Double amount,
        String currency,
        String toIban,
        PaymentStatus status,
        LocalDateTime createdAt,
        String accountId
) {
}
