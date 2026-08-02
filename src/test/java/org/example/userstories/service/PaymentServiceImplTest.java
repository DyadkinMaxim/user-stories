package org.example.userstories.service;

import jakarta.persistence.EntityManager;
import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentUpdateRequest;
import org.example.userstories.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void save_setsCreatedAtBeforePersisting() {
        Payment payment = new Payment();
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.save(payment);

        verify(paymentRepository).save(payment);
    }

    @Test
    void update_setsAllFieldsAfterMapping() {
        UUID uuid = UUID.randomUUID();
        Payment payment = new Payment();
        PaymentUpdateRequest paymentUpdateRequest = new PaymentUpdateRequest(
                200.0, "USD", "ACC-002",
                "DE89370400440532013001");
        when(paymentRepository.findById(uuid)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.updatePaymentDetails(uuid, paymentUpdateRequest);

        verify(paymentMapper).updateEntity(paymentUpdateRequest, payment);
        verify(paymentRepository).save(payment);
    }
}
