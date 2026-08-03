package org.example.userstories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userstories.dto.PaymentRequest;
import org.example.userstories.dto.PaymentResponse;
import org.example.userstories.dto.PaymentUpdateRequest;
import org.example.userstories.exception.PaymentNotFoundException;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private PaymentMapper paymentMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_returnsOkWithPaymentList() throws Exception {
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID(), 150.0, "EUR", "ACC-001",
                "DE89370400440532013000", PaymentStatus.PENDING, LocalDateTime.now()
        );

        when(paymentService.findAll(isNull(), isNull(),
                isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(150.0))
                .andExpect(jsonPath("$.content[0].currency").value("EUR"))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void findAll_returnsEmptyListWhenNoPayments()
            throws Exception {
        when(paymentService.findAll(isNull(), isNull(),
                isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void findAllByStatus_returnsAllByStatus()
            throws Exception {
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID(), 150.0, "EUR", "ACC-001",
                "DE89370400440532013000", PaymentStatus.APPROVED, LocalDateTime.now()
        );

        when(paymentService.findAll(eq(PaymentStatus.APPROVED), isNull(),
                isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(150.0))
                .andExpect(jsonPath("$.content[0].currency").value("EUR"))
                .andExpect(jsonPath("$.content[0].status").value("APPROVED"));
    }

    @Test
    void findAllByStatus_invalidStatus()
            throws Exception {
        Payment payment = new Payment();

        when(paymentService.findAll(any(PaymentStatus.class), isNull(),
                isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));

        mockMvc.perform(get("/api/v1/payments")
                        .param("status", "APPROVED11"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllByAmount_returnsAllBetweenAmounts()
            throws Exception {
        Payment payment = new Payment();

        when(paymentService.findAll(any(PaymentStatus.class), eq(100.0),
                eq(200.0), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));
        when(paymentMapper.toResponse(payment)).thenReturn(
                new PaymentResponse(UUID.randomUUID(), 150.0, "EUR", "1234",
                        "AB12", PaymentStatus.APPROVED, LocalDateTime.now()));


        mockMvc.perform(get("/api/v1/payments")
                        .param("status", "APPROVED")
                        .param("minAmount", "100.0")
                        .param("maxAmount", "200.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(150.0));
    }

    @Test
    void findAllByAmount_returnsAllAboveMin()
            throws Exception {
        Payment payment = new Payment();

        when(paymentService.findAll(any(PaymentStatus.class), eq(100.0),
                isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));
        when(paymentMapper.toResponse(payment)).thenReturn(
                new PaymentResponse(UUID.randomUUID(), 150.0, "EUR", "1234",
                        "AB12", PaymentStatus.APPROVED, LocalDateTime.now()));

        mockMvc.perform(get("/api/v1/payments")
                        .param("status", "APPROVED")
                        .param("minAmount", "100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(150.0));
    }

    @Test
    void findAllByAmount_returnsAllLowerMax()
            throws Exception {
        Payment payment = new Payment();

        when(paymentService.findAll(any(PaymentStatus.class), isNull(),
                eq(200.0), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));
        when(paymentMapper.toResponse(payment)).thenReturn(
                new PaymentResponse(UUID.randomUUID(), 150.0, "EUR", "1234",
                        "AB12", PaymentStatus.APPROVED, LocalDateTime.now()));

        mockMvc.perform(get("/api/v1/payments")
                        .param("status", "APPROVED")
                        .param("maxAmount", "200.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(150.0));
    }

    @Test
    void findAllByAmount_returnsAllWithoutStatus()
            throws Exception {
        Payment payment = new Payment();

        when(paymentService.findAll(isNull(), eq(100.0),
                eq(200.0), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payment)));
        when(paymentMapper.toResponse(payment)).thenReturn(
                new PaymentResponse(UUID.randomUUID(), 150.0, "EUR", "1234",
                        "AB12", PaymentStatus.APPROVED, LocalDateTime.now()));

        mockMvc.perform(get("/api/v1/payments")
                        .param("minAmount", "100.0")
                        .param("maxAmount", "200.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(150.0));
    }

    @Test
    void create_returnsCreatedWithSavedPayment() throws Exception {
        PaymentRequest request = new PaymentRequest(200.0, "USD", "ACC-002", "FR7630006000011234567890189");
        Payment entity = new Payment();
        Payment saved = new Payment();
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID(), 200.0, "USD", "ACC-002",
                "FR7630006000011234567890189", PaymentStatus.PENDING, LocalDateTime.now()
        );

        when(paymentMapper.toEntity(request)).thenReturn(entity);
        when(paymentService.save(entity)).thenReturn(saved);
        when(paymentMapper.toResponse(saved)).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void create_returns400_invalidIban() throws Exception {
        PaymentRequest request = new PaymentRequest(200.0, "USD",
                "ACC-002", "F1R7630006000011234567890189");

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.detail")
                        .value(containsString("IBAN must start with 2 letters" +
                                " followed by 2 digits followed by alphanumeric.")));
    }

    @Test
    void findById_returnsOkWithPayment() throws Exception {
        Payment payment = new Payment();
        UUID id = UUID.randomUUID();
        PaymentResponse response = new PaymentResponse(id, 150.0, "EUR", "ACC-001",
                "DE89370400440532013000", PaymentStatus.PENDING, LocalDateTime.now());

        when(paymentService.findById(any())).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.accountId").value("ACC-001"))
                .andExpect(jsonPath("$.toIban").value("DE89370400440532013000"))
                .andExpect(jsonPath("$.status").value("PENDING"));


    }

    @Test
    void findById_whenPaymentNotFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(paymentService.findById(any(UUID.class)))
                .thenThrow(new PaymentNotFoundException(id));

        mockMvc.perform(get("/api/v1/payments/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_returnsOkWithUpdatedPayment() throws Exception {
        UUID id = UUID.randomUUID();
        Payment updated = new Payment();
        PaymentResponse response = new PaymentResponse(
                id, 150.0, "EUR", "ACC-001",
                "DE89370400440532013000", PaymentStatus.APPROVED, LocalDateTime.now()
        );

        when(paymentService.updateStatus(any(UUID.class), eq(PaymentStatus.APPROVED) )).thenReturn(updated);
        when(paymentMapper.toResponse(updated)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/payments/{id}/status", id)
                        .param("status", PaymentStatus.APPROVED.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateStatus_whenPaymentNotFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(paymentService.updateStatus(any(UUID.class), any(PaymentStatus.class)))
                .thenThrow(new PaymentNotFoundException(id));

        mockMvc.perform(patch("/api/v1/payments/{id}/status", id)
                        .param("status", PaymentStatus.APPROVED.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePaymentDetails_returnsOkWithUpdatedPayment() throws Exception {
        UUID id = UUID.randomUUID();
        Payment updated = new Payment();
        PaymentUpdateRequest request = new PaymentUpdateRequest(
                200.0, "USD", "ACC-002",
                "DE89370400440532013001"
        );
        PaymentResponse response = new PaymentResponse(
                id, 200.0, "USD", "ACC-002",
                "DE89370400440532013001", PaymentStatus.APPROVED, LocalDateTime.now()
        );

        when(paymentService.updatePaymentDetails(
                any(UUID.class), any(PaymentUpdateRequest.class))).thenReturn(updated);
        when(paymentMapper.toResponse(updated)).thenReturn(response);

        mockMvc.perform(put("/api/v1/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.accountId").value("ACC-002"))
                .andExpect(jsonPath("$.toIban").value("DE89370400440532013001"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updatePaymentDetails_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        PaymentUpdateRequest request = new PaymentUpdateRequest(
                200.0, "USD", "ACC-002",
                "DE89370400440532013001"
        );

        when(paymentService.updatePaymentDetails(
                any(UUID.class), any(PaymentUpdateRequest.class)))
                .thenThrow(new PaymentNotFoundException(id));

        mockMvc.perform(put("/api/v1/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePaymentDetails_invalidIban() throws Exception {
        UUID id = UUID.randomUUID();
        PaymentUpdateRequest request = new PaymentUpdateRequest(200.0, "USD",
                "ACC-002", "F1R7630006000011234567890189");

        mockMvc.perform(put("/api/v1/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.detail")
                        .value(containsString("IBAN must start with 2 letters" +
                                " followed by 2 digits followed by alphanumeric.")));
    }

    @Test
    void deletePaymentSoft_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/payments/soft/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePaymentSoft_whenPaymentNotFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new PaymentNotFoundException(id))
                .when(paymentService).softDelete(any(UUID.class));

        mockMvc.perform(delete("/api/v1/payments/soft/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePaymentForce_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/payments/hard/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePaymentForce_whenPaymentNotFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new PaymentNotFoundException(id))
                .when(paymentService).hardDelete(any(UUID.class));

        mockMvc.perform(delete("/api/v1/payments/hard/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStats_returnsOkWithMap() throws Exception {
        Map<PaymentStatus, Long> paymentsByStatus = new HashMap<>();
        paymentsByStatus.put(PaymentStatus.PENDING, 1L);
        paymentsByStatus.put(PaymentStatus.APPROVED, 2L);

        when(paymentService.getStats()).thenReturn(paymentsByStatus);

        mockMvc.perform(get("/api/v1/payments/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PENDING").value(1))
                .andExpect(jsonPath("$.APPROVED").value(2));
    }
}
