package org.example.userstories.dto;

import org.example.userstories.model.Account;
import org.example.userstories.model.Payment;

import java.util.List;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String ownerName,
        String iban,
        Double balance,
        List<PaymentResponse> payments
) {
}