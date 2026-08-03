package org.example.userstories.dto;

import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        Double amount,
        String currency,
        String accountId,
        String toIban,
        PaymentStatus status,
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
