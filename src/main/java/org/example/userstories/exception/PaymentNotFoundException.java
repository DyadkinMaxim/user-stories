package org.example.userstories.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(java.util.UUID id) {
        super("Payment not found: " + id);
    }
}
