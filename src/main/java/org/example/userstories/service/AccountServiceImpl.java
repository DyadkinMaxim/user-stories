package org.example.userstories.service;

import lombok.RequiredArgsConstructor;
import org.example.userstories.dto.AccountResponse;
import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.exception.AccountNotFoundException;
import org.example.userstories.exception.InsufficientBalanceException;
import org.example.userstories.mapper.AccountMapper;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Account;
import org.example.userstories.model.Payment;
import org.example.userstories.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException(id));
    }

    @Override
    @Transactional
    public Account addPayment(UUID id, PaymentRequest paymentRequest) {
        Account account = findById(id);
        Payment newPayment = paymentMapper.toEntity(paymentRequest);
        newPayment.setAccount(account);
        if(account.getBalance().compareTo(newPayment.getAmount()) < 0) {
            throw new InsufficientBalanceException(account.getId(), newPayment.getAmount());
        }
        List<Payment> payments = account.getPayments();
        payments.add(newPayment);
        account.setPayments(payments);
        return accountRepository.save(account);
    }


}
