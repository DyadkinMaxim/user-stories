package org.example.userstories.dto;

import org.example.userstories.model.Payment;

public record SavePayment(
        Payment payment,
        boolean isDuplicate
) {}
