package org.example.userstories.controller;

import lombok.RequiredArgsConstructor;
import org.example.userstories.dto.AccountResponse;
import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.mapper.AccountMapper;
import org.example.userstories.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                accountMapper.toResponse(accountService.findById(id))
        );
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<AccountResponse> addPayment(
            @PathVariable UUID id,
            @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountMapper.toResponse(
                        accountService.addPayment(id, paymentRequest))
                );
    }
}