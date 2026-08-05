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
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getOwnerName(),
                account.getIban(),
                account.getBalance(),
                account.getPayments()
        );
    }
}