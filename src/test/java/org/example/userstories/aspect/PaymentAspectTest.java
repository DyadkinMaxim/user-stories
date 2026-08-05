package org.example.userstories.aspect;

import jakarta.persistence.EntityManager;
import org.example.userstories.dto.PaymentUpdateRequest;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.repository.PaymentRepository;
import org.example.userstories.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentAspectTest {

    @Autowired
    PaymentService paymentService;

    @MockitoBean
    PaymentRepository paymentRepository;

    @MockitoBean
    EntityManager entityManager;

    @MockitoBean
    PaymentMapper paymentMapper;

    @Test
    void save_doesNotThrowAspectException() {
        Payment payment = new Payment();
        when(paymentRepository.findByIdempotencyKey(any()))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any()))
                .thenReturn(payment);

        assertDoesNotThrow(() -> paymentService.save(payment, "1"));
    }

    @Test
    void update_doesNotThrowAspectException() {
        Payment payment = new Payment();
        when(paymentRepository.findById(any()))
                .thenReturn(Optional.of(payment));

        assertDoesNotThrow(() -> paymentService.updatePaymentDetails(
                UUID.randomUUID(), new PaymentUpdateRequest(
                        200.0, "USD", "ACC-002",
                        "DE89370400440532013001"
                )));
    }

    @Test
    void softDelete_doesNotThrowAspectException() {
        Payment payment = new Payment();
        when(paymentRepository.findById(any()))
                .thenReturn(Optional.of(payment));

        assertDoesNotThrow(() -> paymentService.softDelete(
                UUID.randomUUID()));
    }
}
