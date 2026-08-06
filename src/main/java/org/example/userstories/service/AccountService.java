package org.example.userstories.service;

import org.example.userstories.dto.AccountResponse;
import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<AccountResponse> findAll();

    Account findById(UUID id);

    Account addPayment(UUID id, PaymentRequest paymentRequest);
}
