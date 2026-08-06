package org.example.userstories.service;

import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.exception.InsufficientBalanceException;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Account;
import org.example.userstories.model.Payment;
import org.example.userstories.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void addPaymentToAccount_throwsInsufficientBalanceException() {
        UUID accountId = UUID.randomUUID();
        PaymentRequest paymentRequest = new PaymentRequest(
                300.0, "EUR",
                "DE89370400440532013000"
        );
        Payment payment = new Payment();
        payment.setAmount(300.0);
        Account account = new Account();
        account.setBalance(100.0);
        account.setId(accountId);

        when(accountRepository.findById(any(UUID.class))).thenReturn(Optional.of(account));
        when(paymentMapper.toEntity(any(PaymentRequest.class))).thenReturn(payment);

        assertThrows(InsufficientBalanceException.class,
                () -> accountService.addPayment(accountId, paymentRequest));
    }
}
