package org.example.userstories.service;

import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;
import org.example.userstories.dto.PaymentUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

public interface PaymentService {

    Page<Payment> findAll(PaymentStatus status, Double minAmount, Double maxAmount, int pageNumber, int size);

    Payment findById(UUID id);

    Payment save(Payment payment);

    Payment updateStatus(UUID id, PaymentStatus status);

    Payment updatePaymentDetails(UUID id, PaymentUpdateRequest details);

    void hardDelete(UUID id);

    void softDelete(UUID id);

    Map<PaymentStatus, Long> getStats();
}
