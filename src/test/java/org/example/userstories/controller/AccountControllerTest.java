package org.example.userstories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userstories.dto.AccountResponse;
import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.dto.PaymentResponse;
import org.example.userstories.exception.AccountNotFoundException;
import org.example.userstories.mapper.AccountMapper;
import org.example.userstories.model.Account;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AccountMapper accountMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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

        when(accountService.findAll()).thenReturn(List.of(accountResponse));


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

    @Test
    void findById_returnsAccount() throws Exception {
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

        when(accountService.findById(any(UUID.class))).thenReturn(new Account());
        when(accountMapper.toResponse(any(Account.class))).thenReturn(accountResponse);

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.ownerName").value(accountResponse.ownerName()))
                .andExpect(jsonPath("$.payments[0].amount").value(paymentResponse.amount()))
                .andExpect(jsonPath("$.payments[0].toIban").value(paymentResponse.toIban()));
    }

    @Test
    void findById_returnsNotFound() throws Exception {
        when(accountService.findById(any(UUID.class)))
                .thenThrow(AccountNotFoundException.class);

        mockMvc.perform(get("/api/v1/accounts/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
        assertThrows(AccountNotFoundException.class,
                () -> accountService.findById(UUID.randomUUID()));
    }

    @Test
    void addPaymentToAccount_returnsAccountWithNewPayment() throws Exception {
        UUID accountId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        PaymentRequest paymentRequest = new PaymentRequest(
                300.0, "EUR",
                "DE89370400440532013000"
        );
        PaymentResponse paymentResponse1 = new PaymentResponse(
                UUID.randomUUID(), 150.0, "EUR",
                "DE89370400440532013000", PaymentStatus.PENDING, createdAt,
                accountId.toString()
        );
        PaymentResponse paymentResponse2 = new PaymentResponse(
                UUID.randomUUID(), 300.0, "EUR",
                "DE89370400440532013000", PaymentStatus.PENDING, createdAt,
                accountId.toString()
        );
        AccountResponse accountResponse1 = new AccountResponse(
                accountId, "Me", "DE89370400440532013123",
                150.0, List.of(paymentResponse1)
        );

        when(accountService.findById(any(UUID.class))).thenReturn(new Account());
        when(accountMapper.toResponse(any(Account.class))).thenReturn(accountResponse1);

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.payments", hasSize(1)));

        AccountResponse accountResponse2 = new AccountResponse(
                accountId, "Me", "DE89370400440532013123",
                150.0, List.of(paymentResponse1, paymentResponse2)
        );

        when(accountService.addPayment(accountId, paymentRequest)).thenReturn(new Account());
        when(accountMapper.toResponse(any(Account.class))).thenReturn(accountResponse2);

        mockMvc.perform(post("/api/v1/accounts/{id}/payments", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payments", hasSize(2)));

    }
}
