package org.example.userstories.controller;

import org.example.userstories.dto.AccountResponse;
import org.example.userstories.dto.PaymentResponse;
import org.example.userstories.mapper.AccountMapper;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Account;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AccountMapper accountMapper;

    @MockitoBean
    private PaymentMapper paymentMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    void findAll_returnsAllAccounts() throws Exception {
        UUID accountId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        PaymentResponse paymentResponse = new PaymentResponse(
                UUID.randomUUID(), 150.0, "EUR",
                "DE89370400440532013000", PaymentStatus.PENDING, createdAt,
                "1234"
        );
        AccountResponse accountResponse = new AccountResponse(
                accountId, "Me", "DE89370400440532013123",
                150.0, List.of(paymentResponse)
        );

        when(accountService.findAll()).thenReturn(List.of(new Account()));
        when(accountMapper.toResponse(any(Account.class))).thenReturn(accountResponse);

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(accountId.toString()))
                .andExpect(jsonPath("$[0].balance").value(150.0))
                .andExpect(jsonPath("$[0].payments[0].amount").value(150.0))
                .andExpect(jsonPath("$[0].payments[0].status")
                        .value(PaymentStatus.PENDING.toString()))
                .andExpect(jsonPath("$[0].payments[0].createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$[0].payments[0].currency").value("EUR"));
    }

    @Test
    void findAll_returnsEmptyList() throws Exception {
        when(accountService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

    }
}
