package org.example.userstories.service;

import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentStatus;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    List<Payment> findAll();

    Payment findById(UUID id);

    Payment save(Payment payment);

    Payment updateStatus(UUID id, PaymentStatus status);
}
