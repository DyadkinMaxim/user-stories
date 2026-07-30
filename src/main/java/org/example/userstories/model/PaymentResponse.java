package org.example.userstories.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        Double amount,
        String currency,
        String accountId,
        String toIban,
        String status,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getAccountId(),
                payment.getToIban(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
