package org.example.userstories.service;

import org.example.userstories.model.Payment;

import java.util.List;

public interface PaymentService {

    List<Payment> findAll();

    Payment save(Payment payment);
}
