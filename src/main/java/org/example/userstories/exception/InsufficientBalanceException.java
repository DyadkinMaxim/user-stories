package org.example.userstories.exception;

import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(UUID id, Double amount) {
        super(String.format(
                "Insufficient balance for user %s, for payment amount: %s",
                id.toString(), amount.toString()));
    }
}
