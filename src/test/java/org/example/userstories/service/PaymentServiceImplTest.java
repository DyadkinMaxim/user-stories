package org.example.userstories.service;

import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.model.PaymentUpdateRequest;
import org.example.userstories.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void findAll_returnsAllPaymentsFromRepository() {
        Payment payment = new Payment();
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findAll(null);

        assertThat(result).hasSize(1).containsExactly(payment);
        verify(paymentRepository).findAll();
    }

    @Test
    void findAllByStatus_returnsAllByStatus()
            throws Exception {
        Payment payment = new Payment();
        when(paymentRepository.findAllByStatus(PaymentStatus.PENDING)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findAll(PaymentStatus.PENDING);

        assertThat(result).hasSize(1).containsExactly(payment);
        verify(paymentRepository).findAllByStatus(PaymentStatus.PENDING);
    }

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
        Payment payment = new Payment(uuid, 200.0, "USD", "ACC-002",
                "DE89370400440532013001", PaymentStatus.PENDING, LocalDateTime.now());
        PaymentUpdateRequest paymentUpdateRequest = new PaymentUpdateRequest(
                200.0, "USD", "ACC-002",
                "DE89370400440532013001");
        when(paymentRepository.findById(uuid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toEntity(paymentUpdateRequest)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment updated = paymentService.updatePaymentDetails(uuid, paymentUpdateRequest);

        verify(paymentRepository).save(payment);
        assertThat(updated.getAmount()).isEqualTo(200.0);
        assertThat(updated.getCurrency()).isEqualTo("USD");
        assertThat(updated.getAccountId()).isEqualTo("ACC-002");
        assertThat(updated.getToIban()).isEqualTo("DE89370400440532013001");
    }
}
