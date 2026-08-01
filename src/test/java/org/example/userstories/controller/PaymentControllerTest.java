package org.example.userstories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userstories.exception.PaymentNotFoundException;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentRequest;
import org.example.userstories.model.PaymentResponse;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        when(paymentService.findAll(null)).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(150.0))
                .andExpect(jsonPath("$[0].currency").value("EUR"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void findAll_returnsEmptyListWhenNoPayments()
            throws Exception {
        when(paymentService.findAll(null))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void findAllByStatus_returnsAllByStatus()
            throws Exception {
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID(), 150.0, "EUR", "ACC-001",
                "DE89370400440532013000", PaymentStatus.APPROVED, LocalDateTime.now()
        );

        when(paymentService.findAll(PaymentStatus.APPROVED)).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(150.0))
                .andExpect(jsonPath("$[0].currency").value("EUR"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void findAllByStatus_invalidStatus()
            throws Exception {
        Payment payment = new Payment();

        when(paymentService.findAll(PaymentStatus.APPROVED)).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/v1/payments")
                        .param("status", "APPROVED11"))
                .andExpect(status().isBadRequest());
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
}
