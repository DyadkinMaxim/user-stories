package org.example.userstories.service;

import lombok.RequiredArgsConstructor;
import org.example.userstories.model.Account;
import org.example.userstories.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }
}
