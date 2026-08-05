package org.example.userstories.aspect;

import org.example.userstories.model.Payment;
import org.example.userstories.repository.PaymentRepository;
import org.example.userstories.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentAspectTest {

    @Autowired
    PaymentService paymentService;

    @MockitoBean
    PaymentRepository paymentRepository;

    @Test
    void save_doesnotThrowAspectException() {
        Payment payment = new Payment();
        when(paymentRepository.findByIdempotencyKey(any()))
                .thenReturn(Optional.empty());   // ← not duplicate ✅
        when(paymentRepository.save(any()))
                .thenReturn(payment);

        assertDoesNotThrow(() -> paymentService.save(payment, eq("1")));
    }
}
