package org.example.userstories.service;

import org.example.userstories.model.Payment;
import org.example.userstories.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void findAll_returnsAllPaymentsFromRepository() {
        Payment payment = new Payment();
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findAll();

        assertThat(result).hasSize(1).containsExactly(payment);
        verify(paymentRepository).findAll();
    }

    @Test
    void save_setsCreatedAtBeforePersisting() {
        Payment payment = new Payment();
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.save(payment);

        verify(paymentRepository).save(payment);
    }
}
