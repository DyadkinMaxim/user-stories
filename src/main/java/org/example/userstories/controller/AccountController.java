package org.example.userstories.controller;

import lombok.RequiredArgsConstructor;
import org.example.userstories.dto.AccountResponse;
import org.example.userstories.mapper.AccountMapper;
import org.example.userstories.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll().stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList()));
    }
}
