package org.example.userstories.model;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(java.util.UUID id) {
        super("Payment not found: " + id);
    }
}
