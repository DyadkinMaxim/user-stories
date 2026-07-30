package org.example.userstories.payment;

import java.time.LocalDateTime;

public record PaymentResponse(
        String id,
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
